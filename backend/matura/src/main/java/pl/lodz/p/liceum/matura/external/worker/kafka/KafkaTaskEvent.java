package pl.lodz.p.liceum.matura.external.worker.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import pl.lodz.p.liceum.matura.config.KafkaConfiguration;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskEvent;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskSentForProcessingEvent;


@AllArgsConstructor
public class KafkaTaskEvent {

    private final KafkaProperties kafkaProperties;
    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public String send(TaskEvent taskEvent) {
        kafkaTemplate.send(kafkaProperties.getCommandTopic(), taskEvent);

        return "TaskEvent Send Successfully";
    }

    public String send(TaskSentForProcessingEvent event) {
        kafkaTemplate.send(kafkaProperties.getCommandTopic(), event);

        return "TaskSentForProcessingEvent Send Successfully";
    }
}
