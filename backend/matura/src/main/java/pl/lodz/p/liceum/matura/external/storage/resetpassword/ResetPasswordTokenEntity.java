package pl.lodz.p.liceum.matura.external.storage.resetpassword;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.ZonedDateTime;

@Entity
@Table()
@Getter
@Setter
@NoArgsConstructor
public class ResetPasswordTokenEntity {
    @Id
    @SequenceGenerator(
            name = "reset_password_token_id_seq",
            sequenceName = "reset_password_token_id_seq",
            allocationSize = 1
    )
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "reset_password_token_id_seq"
    )
    Integer id;
    @Column(
            nullable = false
    )
    String email;
    @Column(
            nullable = false
    )
    String token;
    @Column(
            nullable = false
    )
    ZonedDateTime expiresAt;

    public ResetPasswordTokenEntity(final String email, final String token, final ZonedDateTime expiresAt) {
        this.email = email;
        this.token = token;
        this.expiresAt = expiresAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ResetPasswordTokenEntity that = (ResetPasswordTokenEntity) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return 0;
    }
}
