package khanhnq.project.clinicbookingmanagementsystem.service.serviceImpl;

import khanhnq.project.clinicbookingmanagementsystem.entity.Specialization;
import khanhnq.project.clinicbookingmanagementsystem.entity.User;
import khanhnq.project.clinicbookingmanagementsystem.entity.WorkSchedule;
import khanhnq.project.clinicbookingmanagementsystem.repository.SpecializationRepository;
import khanhnq.project.clinicbookingmanagementsystem.repository.UserRepository;
import khanhnq.project.clinicbookingmanagementsystem.request.DoctorInformationRequest;
import khanhnq.project.clinicbookingmanagementsystem.response.MessageResponse;
import khanhnq.project.clinicbookingmanagementsystem.service.AuthService;
import khanhnq.project.clinicbookingmanagementsystem.service.DoctorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class DoctorServiceImpl implements DoctorService {

    private final AuthService authService;
    private final SpecializationRepository specializationRepository;
    private final UserRepository userRepository;

    public DoctorServiceImpl(AuthService authService,
                             SpecializationRepository specializationRepository,
                             UserRepository userRepository) {
        this.authService = authService;
        this.specializationRepository = specializationRepository;
        this.userRepository = userRepository;
    }

    @Override
    public ResponseEntity<String> addDoctorInformation(DoctorInformationRequest doctorInformationRequest) {
        User currentUser = authService.getCurrentUser();
        Specialization specialization = specializationRepository.findSpecializationBySpecializationName(doctorInformationRequest.getSpecialization());
        Set<WorkSchedule> workSchedules = doctorInformationRequest.getWorkSchedules()
                .stream()
                .map(workScheduleRequest -> {
                    WorkSchedule workSchedule = WorkSchedule.builder()
                            .startTime(workScheduleRequest.getStartTime())
                            .endTime(workScheduleRequest.getEndTime())
                            .user(currentUser)
                            .build();
                    return workSchedule;
                }).collect(Collectors.toSet());
        currentUser.setSpecialization(specialization);
        currentUser.setWorkSchedules(workSchedules);
        userRepository.save(currentUser);
        return MessageResponse.getResponseMessage("Update doctor information successfully.", HttpStatus.OK);
    }
}