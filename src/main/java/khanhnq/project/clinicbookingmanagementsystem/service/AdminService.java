package khanhnq.project.clinicbookingmanagementsystem.service;

import khanhnq.project.clinicbookingmanagementsystem.response.DoctorResponse;
import khanhnq.project.clinicbookingmanagementsystem.response.RequestDoctorResponse;
import khanhnq.project.clinicbookingmanagementsystem.response.UserResponse;
import org.springframework.http.ResponseEntity;
import java.util.List;

public interface AdminService {
    ResponseEntity<String> approveRequestDoctor(Long userId);
    ResponseEntity<List<UserResponse>> getAllUsers();
    ResponseEntity<List<RequestDoctorResponse>> getAllRequestDoctors();
    ResponseEntity<List<DoctorResponse>> getAllDoctors();

}
