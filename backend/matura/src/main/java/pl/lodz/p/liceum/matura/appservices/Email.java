package pl.lodz.p.liceum.matura.appservices;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.external.email.SendEmailCommand;

import java.util.List;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Email {

    String subject;
    List<String> to;
    List<String> cc;
    List<String> bcc;
    String body;

}
