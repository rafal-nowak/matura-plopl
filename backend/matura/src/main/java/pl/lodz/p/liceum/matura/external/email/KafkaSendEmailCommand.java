package pl.lodz.p.liceum.matura.external.email;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskEvent;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskSentForProcessingEvent;


@AllArgsConstructor
@Service
public class KafkaSendEmailCommand {

    private final KafkaProperties kafkaProperties;
    private KafkaTemplate<String, SendEmailCommand> kafkaTemplate;

    public String send(SendEmailCommand sendEmailCommand) {
        kafkaTemplate.send(kafkaProperties.getEmailCommandTopic(), sendEmailCommand);

        return "SendEmailCommand Send Successfully";
    }

}
