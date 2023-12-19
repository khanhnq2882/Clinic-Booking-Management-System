package khanhnq.project.clinicbookingmanagementsystem.service.serviceImpl;

import khanhnq.project.clinicbookingmanagementsystem.common.FunctionsCommon;
import khanhnq.project.clinicbookingmanagementsystem.dto.BookingDTO;
import khanhnq.project.clinicbookingmanagementsystem.entity.*;
import khanhnq.project.clinicbookingmanagementsystem.exception.ResourceException;
import khanhnq.project.clinicbookingmanagementsystem.mapper.BookingMapper;
import khanhnq.project.clinicbookingmanagementsystem.mapper.UserMapper;
import khanhnq.project.clinicbookingmanagementsystem.repository.*;
import khanhnq.project.clinicbookingmanagementsystem.request.DoctorInformationRequest;
import khanhnq.project.clinicbookingmanagementsystem.response.AddressResponse;
import khanhnq.project.clinicbookingmanagementsystem.response.BookingResponse;
import khanhnq.project.clinicbookingmanagementsystem.service.AuthService;
import khanhnq.project.clinicbookingmanagementsystem.service.DoctorService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class DoctorServiceImpl implements DoctorService {
    private final AuthService authService;
    private final WardRepository wardRepository;
    private final SkillRepository skillRepository;
    private final BookingRepository bookingRepository;
    private final AddressRepository addressRepository;
    private final UserRepository userRepository;
    private final FunctionsCommon functionsCommon;

    @Override
    public String updateDoctorInformation(DoctorInformationRequest doctorInformationRequest) {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRoles().stream().noneMatch(role -> role.getRoleName().name().equals("ROLE_DOCTOR"))) {
            throw new ResourceException("You don't have permission to do this.", HttpStatus.UNAUTHORIZED);
        }
        UserMapper.USER_MAPPER.mapToDoctor(currentUser, doctorInformationRequest);
        currentUser.setAddress(Address.builder()
                .specificAddress(doctorInformationRequest.getSpecificAddress())
                .ward(wardRepository.findById(doctorInformationRequest.getWardId()).orElse(null))
                .build());
        Set<WorkSchedule> workSchedules = doctorInformationRequest.getWorkSchedules().stream()
                .map(workScheduleDTO -> WorkSchedule.builder()
                        .startTime(LocalTime.parse(workScheduleDTO.getStartTime()))
                        .endTime(LocalTime.parse(workScheduleDTO.getEndTime()))
                        .user(currentUser)
                        .build())
                .collect(Collectors.toSet());
        currentUser.setWorkSchedules(filterWorkSchedules(workSchedules, currentUser.getSpecialization().getSpecializationId()));
        currentUser.setSkills(doctorInformationRequest.getSkillIds()
                        .stream()
                        .map(id -> skillRepository.findById(id).orElse(null))
                        .collect(Collectors.toSet()));
        userRepository.save(currentUser);
        return "Update information successfully.";
    }

    @Override
    public String confirmedBooking(Long bookingId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRoles().stream().noneMatch(role -> role.getRoleName().name().equals("ROLE_DOCTOR"))) {
            throw new ResourceException("You don't have permission to do this.", HttpStatus.UNAUTHORIZED);
        }
        bookingRepository.confirmedBooking(bookingId);
        return "Successful booking confirmation.";
    }

    @Override
    public String cancelledBooking(Long bookingId) {
        User currentUser = authService.getCurrentUser();
        if (currentUser.getRoles().stream().noneMatch(role -> role.getRoleName().name().equals("ROLE_DOCTOR"))) {
            throw new ResourceException("You don't have permission to do this.", HttpStatus.UNAUTHORIZED);
        }
        bookingRepository.cancelledBooking(bookingId);
        return "Successful booking cancelled. Please provide reasons why booking is cancelled for the patient.";
    }

    @Override
    public BookingResponse getAllBookings(int page, int size, String[] sorts) {
        User currentUser = authService.getCurrentUser();
        Page<Booking> bookingPage = bookingRepository.getAllBookings(currentUser.getUserId() , functionsCommon.pagingSort(page, size, sorts));
        List<BookingDTO> bookings = bookingPage.getContent()
                .stream()
                .map(booking -> {
                    BookingDTO bookingDTO = BookingMapper.BOOKING_MAPPER.mapToBookingDTO(booking);
                    bookingDTO.setUserAddress(getAddress(booking));
                    bookingDTO.setStartTime(DateTimeFormatter.ofPattern("HH:mm").format(booking.getWorkSchedule().getStartTime()));
                    bookingDTO.setEndTime(DateTimeFormatter.ofPattern("HH:mm").format(booking.getWorkSchedule().getEndTime()));
                    return bookingDTO;
                }).toList();
        return BookingResponse.builder()
                .totalItems(bookingPage.getTotalElements())
                .totalPages(bookingPage.getTotalPages())
                .currentPage(bookingPage.getNumber())
                .bookings(bookings)
                .build();
    }


    public AddressResponse getAddress(Booking booking) {
        Address address = addressRepository.findById(booking.getAddress().getAddressId()).orElse(null);
        new AddressResponse();
        return AddressResponse.builder()
                .addressId(Objects.requireNonNull(address).getAddressId())
                .specificAddress(address.getSpecificAddress())
                .wardName(address.getWard().getWardName())
                .districtName(address.getWard().getDistrict().getDistrictName())
                .cityName(address.getWard().getDistrict().getCity().getCityName())
                .build();
    }

    public Set<WorkSchedule> filterWorkSchedules(Set<WorkSchedule> workSchedulesRequest, Long specializationId) {
        Set<WorkSchedule> resultWorkSchedule = workSchedulesRequest;
        for (User doctor : functionsCommon.groupDoctorsBySpecialization().get(specializationId)) {
            resultWorkSchedule = resultWorkSchedule.stream()
                    .filter(request -> functionsCommon.groupWorkScheduleByDoctor().get(doctor.getUserId()).stream()
                            .noneMatch(workSchedule -> workSchedule.getStartTime().equals(request.getStartTime())
                                    && workSchedule.getEndTime().equals(request.getEndTime())))
                    .collect(Collectors.toSet());
        }
        return resultWorkSchedule;
    }

}
