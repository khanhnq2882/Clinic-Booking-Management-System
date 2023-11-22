package khanhnq.project.clinicbookingmanagementsystem.controller;

import khanhnq.project.clinicbookingmanagementsystem.entity.File;
import khanhnq.project.clinicbookingmanagementsystem.entity.Skill;
import khanhnq.project.clinicbookingmanagementsystem.entity.Specialization;
import khanhnq.project.clinicbookingmanagementsystem.repository.SkillRepository;
import khanhnq.project.clinicbookingmanagementsystem.repository.SpecializationRepository;
import khanhnq.project.clinicbookingmanagementsystem.response.DoctorResponse;
import khanhnq.project.clinicbookingmanagementsystem.response.RequestDoctorResponse;
import khanhnq.project.clinicbookingmanagementsystem.response.UserResponse;
import khanhnq.project.clinicbookingmanagementsystem.service.AdminService;
import khanhnq.project.clinicbookingmanagementsystem.service.AuthService;
import khanhnq.project.clinicbookingmanagementsystem.service.FileService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200", maxAge = 3600, allowCredentials = "true")
@RestController
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;
    private final AuthService authService;
    private final FileService fileService;
    private final SkillRepository skillRepository;
    private final SpecializationRepository specializationRepository;

    public AdminController(AdminService adminService,
                           SkillRepository skillRepository,
                           SpecializationRepository specializationRepository,
                           AuthService authService,
                           FileService fileService) {
        this.adminService = adminService;
        this.skillRepository = skillRepository;
        this.specializationRepository = specializationRepository;
        this.authService = authService;
        this.fileService = fileService;
    }

    @GetMapping("")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public String adminHome(){
        return "Welcome Admin";
    }

    @GetMapping("/skills")
    public ResponseEntity<List<Skill>> getAllSkills() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(skillRepository.findAll());
    }

    @GetMapping("/specializations")
    public ResponseEntity<List<Specialization>> getAllSpecializations() {
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(specializationRepository.findAll());
    }

    @PostMapping("/approve-request-doctor/{userId}")
    public ResponseEntity<String> updateUserRoles(@PathVariable("userId") Long userId) {
        return adminService.approveRequestDoctor(userId);
    }

    @GetMapping("/get-all-users")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        return adminService.getAllUsers();
    }

    @GetMapping("/get-all-request-doctors")
    public ResponseEntity<List<RequestDoctorResponse>> getAllRequestDoctors() {
        return adminService.getAllRequestDoctors();
    }

    @GetMapping("/files/{fileId}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long fileId) {
        File file = fileService.getFileById(fileId);
        String fileName = file.getFilePath().split("/")[2];
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(file.getData());
    }

    @GetMapping("/get-all-doctors")
    public ResponseEntity<List<DoctorResponse>> getAllDoctors() {
        return adminService.getAllDoctors();
    }

}
