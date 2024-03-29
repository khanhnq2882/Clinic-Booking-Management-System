package khanhnq.project.clinicbookingmanagementsystem.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "day_of_week")
@Builder
public class DaysOfWeek {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dayOfWeekId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private DayOfWeek dayOfWeek;

    @Column
    private int numberOfShiftsPerDay;

    @OneToMany(mappedBy = "daysOfWeek", cascade = CascadeType.ALL)
    private List<WorkSchedule> workSchedules;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
