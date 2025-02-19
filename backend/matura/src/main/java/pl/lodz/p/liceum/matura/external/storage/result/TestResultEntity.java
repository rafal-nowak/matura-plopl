package pl.lodz.p.liceum.matura.external.storage.result;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import pl.lodz.p.liceum.matura.domain.result.Verdict;

import java.time.ZonedDateTime;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
public class TestResultEntity {
    @Id
    @SequenceGenerator(
            name = "test_result_id_seq",
            sequenceName = "test_result_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "test_result_id_seq"
    )
    private Integer id;

    @Column(name="subtask_result_id", nullable = false)
    private Integer subtaskResultId;
    @Column(name="verdict", nullable = false)
    private Verdict verdict;
    @Column(name="time", nullable = true)
    private Integer time;
    @Column(name="memory", nullable = true)
    private Integer memory;
    @Column(name="message", nullable = true, columnDefinition = "TEXT")
    private String message;
    @Column(name="created_at", nullable = false)
    private ZonedDateTime createdAt;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TestResultEntity that = (TestResultEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
