package pl.lodz.p.liceum.matura.external.storage.result;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class SubtaskResultEntity {
    @Id
    @SequenceGenerator(
            name = "subtask_result_id_seq",
            sequenceName = "subtask_result_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "subtask_result_id_seq"
    )
    private Integer id;

    @Column(name="submission_id", nullable = false)
    private Integer submissionId;
    @Column(name="subtask_number", nullable = false)
    private Integer subtaskNumber;
    @Column(name="description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name="score", nullable = false)
    private Integer score;
    @Column(name="created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SubtaskResultEntity that = (SubtaskResultEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
