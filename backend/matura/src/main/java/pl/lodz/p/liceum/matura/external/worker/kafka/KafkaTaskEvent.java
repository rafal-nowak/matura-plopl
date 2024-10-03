package pl.lodz.p.liceum.matura.external.worker.kafka;

import lombok.AllArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import pl.lodz.p.liceum.matura.config.KafkaConfiguration;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskEvent;
import pl.lodz.p.liceum.matura.external.worker.task.events.TaskSentForProcessingEvent;


@AllArgsConstructor
public class KafkaTaskEvent {

    private KafkaTemplate<String, TaskEvent> kafkaTemplate;

    public String send(TaskEvent taskEvent) {
        kafkaTemplate.send(KafkaConfiguration.TASKS_OUTBOUND_TOPIC, taskEvent);

        return "TaskEvent Send Successfully";
    }

    public String send(TaskSentForProcessingEvent event) {
        kafkaTemplate.send(KafkaConfiguration.TASKS_OUTBOUND_TOPIC, event);

        return "TaskSentForProcessingEvent Send Successfully";
    }
}
