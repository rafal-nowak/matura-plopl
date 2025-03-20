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
    static private Long counter = 0L;

    public String send(TaskEvent taskEvent) {
        counter++;
        String key = "key-" + counter;  // Unique key
        kafkaTemplate.send(kafkaProperties.getCommandTopic(), key, taskEvent);

        return "TaskEvent sent successfully";
    }
}
