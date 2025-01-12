package pl.lodz.p.liceum.matura.external;


import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.external.email.SendEmailCommand;

@Log
@Service
@RequiredArgsConstructor
public class KafkaSendEmailCommandProcessor {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, SendEmailCommand> kafkaTemplate;


    @KafkaListener(topics = "#{kafkaProperties.emailCommandTopic}", groupId = "#{kafkaProperties.groupId}", containerFactory = "emailKafkaListenerFactory")
    public void onReceive(SendEmailCommand sendEmailCommand) {

        log.info("Received SendEmailCommand: " + sendEmailCommand);

    }
}
