package khanhnq.project.clinicbookingmanagementsystem.request;

import khanhnq.project.clinicbookingmanagementsystem.dto.WorkScheduleDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.DayOfWeek;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterWorkScheduleRequest {
    private DayOfWeek dayOfWeek;
    private int numberOfShiftsPerDay;
    private List<WorkScheduleDTO> workSchedules;
}
