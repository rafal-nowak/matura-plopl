package pl.lodz.p.liceum.matura.external;


import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.TopicPartition;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerAwareRebalanceListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.domain.*;
import pl.lodz.p.liceum.matura.external.worker.task.events.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Log
@Service
@RequiredArgsConstructor
public class KafkaTaskProcessor implements ConsumerAwareRebalanceListener {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private final TaskExecutor taskExecutor;

    private final ExecutorService taskExecutorService = Executors.newFixedThreadPool(5);


    @KafkaListener(topics = "#{kafkaProperties.commandTopic}", groupId = "#{kafkaProperties.groupId}", containerFactory = "taskKafkaListenerFactory")
    public void onReceive(ConsumerRecord<String, TaskEvent> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        log.info("📩 Received task event: " + record.value());

        taskExecutorService.submit(() -> processTask(record, acknowledgment, consumer));
    }

    private void processTask(ConsumerRecord<String, TaskEvent> record, Acknowledgment acknowledgment, Consumer<?, ?> consumer) {
        try {
            TaskEvent taskEvent = record.value();
            if (taskEvent instanceof TaskSentForProcessingEvent taskSentForProcessingEvent) {
                log.info("Received TaskSentForProcessingEvent: " + taskEvent);

                Task task = new Task(taskEvent.getTaskId(), taskEvent.getWorkspaceUrl());

                for (int i = 1; i <= taskSentForProcessingEvent.getNumberOfSubtasks(); i++) {
                    Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), i, TestType.FULL);
                    List<TestResult> testResults = taskExecutor.executeSubtask(subtask);

                    kafkaTemplate.executeInTransaction(operations -> {
                        operations.send(
                                kafkaProperties.getReportTopic(),
                                new SubtaskFullProcessingCompleteEvent(taskEvent.getTaskId(), taskSentForProcessingEvent.getSubmissionId(), subtask.getWorkspaceUrl(), subtask.getNumber(), testResults)
                        );
                        return true;
                    });
                }

                kafkaTemplate.executeInTransaction(operations -> {
                    operations.send(
                            kafkaProperties.getReportTopic(),
                            new TaskProcessingCompleteEvent(task.getTaskId(), taskSentForProcessingEvent.getSubmissionId(), task.getWorkspaceUrl())
                    );
                    acknowledgment.acknowledge();
                    log.info("🔗 Offset committed for task: " + taskEvent);
                    return true;
                });

            } else if (taskEvent instanceof SubtaskSentForFastProcessingEvent subtaskSentForFastProcessingEvent) {
                log.info("Received SubtaskSentForFastProcessingEvent: " + taskEvent);
                Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), subtaskSentForFastProcessingEvent.getNumber(), TestType.FAST);
                List<TestResult> testResults = taskExecutor.executeSubtask(subtask);

                kafkaTemplate.executeInTransaction(operations -> {
                    operations.send(
                            kafkaProperties.getReportTopic(),
                            new SubtaskFastProcessingCompleteEvent(taskEvent.getTaskId(), subtaskSentForFastProcessingEvent.getSubmissionId(), subtask.getWorkspaceUrl(), subtask.getNumber(), testResults)
                    );
                    acknowledgment.acknowledge();
                    log.info("🔗 Offset committed for task: " + taskEvent);
                    return true;
                });

            } else if (taskEvent instanceof SubtaskSentForFullProcessingEvent subtaskSentForFullProcessingEvent) {
                log.info("Received SubtaskSentForFullProcessingEvent: " + taskEvent);

                Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), subtaskSentForFullProcessingEvent.getNumber(), TestType.FULL);
                List<TestResult> testResults = taskExecutor.executeSubtask(subtask);

                kafkaTemplate.executeInTransaction(operations -> {
                    operations.send(
                            kafkaProperties.getReportTopic(),
                            new SubtaskFullProcessingCompleteEvent(taskEvent.getTaskId(), subtaskSentForFullProcessingEvent.getSubmissionId(), subtask.getWorkspaceUrl(), subtask.getNumber(), testResults)
                    );
                    acknowledgment.acknowledge();
                    log.info("🔗 Offset committed for task: " + taskEvent);
                    return true;
                });

            } else {
                log.info("Received TaskEvent: " + taskEvent);
            }
        } catch (Exception e) {
            log.severe("❌ Error during task processing: " + record.value() + " - " + e.getMessage());
        }
    }

    @Override
    public void onPartitionsRevokedBeforeCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        log.info("🔁 Partitions revoked before commit: " + partitions);
    }

    @Override
    public void onPartitionsRevokedAfterCommit(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        log.info("🔁 Partitions revoked after commit: " + partitions);
    }

    @Override
    public void onPartitionsAssigned(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        log.info("🔗 Partitions assigned: " + partitions);
    }

    @Override
    public void onPartitionsLost(Consumer<?, ?> consumer, Collection<TopicPartition> partitions) {
        log.warning("⚠️ Partitions lost: " + partitions);
    }
}
