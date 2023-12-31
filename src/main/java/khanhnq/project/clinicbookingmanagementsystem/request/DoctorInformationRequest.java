package khanhnq.project.clinicbookingmanagementsystem.request;

import khanhnq.project.clinicbookingmanagementsystem.dto.WorkScheduleDTO;
import lombok.*;
import java.util.Date;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DoctorInformationRequest {
    private String firstName;
    private String lastName;
    private Date dateOfBirth;
    private int gender;
    private String phoneNumber;
    private String specificAddress;
    private Long wardId;
    private Set<Long> skillIds;
    private Set<WorkScheduleDTO> workSchedules;
    private String describeExperiences;
}
