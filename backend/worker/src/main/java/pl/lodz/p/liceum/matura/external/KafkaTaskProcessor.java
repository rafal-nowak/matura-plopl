package pl.lodz.p.liceum.matura.external;


import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.domain.*;
import pl.lodz.p.liceum.matura.external.worker.task.events.*;

import java.util.ArrayList;
import java.util.List;

@Log
@Service
@RequiredArgsConstructor
public class KafkaTaskProcessor {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private final TaskExecutor taskExecutor;


    @KafkaListener(topics = "#{kafkaProperties.commandTopic}", groupId = "#{kafkaProperties.groupId}", containerFactory = "taskKafkaListenerFactory")
    public void onReceive(TaskEvent taskEvent) {
        if (taskEvent instanceof TaskSentForProcessingEvent taskSentForProcessingEvent) {
            log.info("Received TaskSentForProcessingEvent: " + taskEvent);

            Task task = new Task(taskEvent.getTaskId(), taskEvent.getWorkspaceUrl());

            for (int i = 1; i <= taskSentForProcessingEvent.getNumberOfSubtasks(); i++) {
                Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), i, TestType.FULL);
                List<TestResult> testResults = taskExecutor.executeSubtask(subtask);

                kafkaTemplate.send(
                        kafkaProperties.getReportTopic(),
                        new SubtaskFullProcessingCompleteEvent(taskEvent.getTaskId(), taskSentForProcessingEvent.getSubmissionId(), subtask.getWorkspaceUrl(), subtask.getNumber(), testResults)
                );
            }

            kafkaTemplate.send(
                    kafkaProperties.getReportTopic(),
                    new TaskProcessingCompleteEvent(task.getTaskId(), taskSentForProcessingEvent.getSubmissionId(), task.getWorkspaceUrl())
            );

        } else if (taskEvent instanceof SubtaskSentForFastProcessingEvent subtaskSentForFastProcessingEvent) {
            log.info("Received SubtaskSentForFastProcessingEvent: " + taskEvent);
            Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), subtaskSentForFastProcessingEvent.getNumber(), TestType.FAST);
            List<TestResult> testResults = taskExecutor.executeSubtask(subtask);

            kafkaTemplate.send(
                    kafkaProperties.getReportTopic(),
                    new SubtaskFastProcessingCompleteEvent(taskEvent.getTaskId(), subtaskSentForFastProcessingEvent.getSubmissionId(), subtask.getWorkspaceUrl(), subtask.getNumber(), testResults)
            );

        } else if (taskEvent instanceof SubtaskSentForFullProcessingEvent subtaskSentForFullProcessingEvent) {
            log.info("Received SubtaskSentForFullProcessingEvent: " + taskEvent);

            Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), subtaskSentForFullProcessingEvent.getNumber(), TestType.FULL);
            List<TestResult> testResults = taskExecutor.executeSubtask(subtask);

            kafkaTemplate.send(
                    kafkaProperties.getReportTopic(),
                    new SubtaskFullProcessingCompleteEvent(taskEvent.getTaskId(), subtaskSentForFullProcessingEvent.getSubmissionId(), subtask.getWorkspaceUrl(), subtask.getNumber(), testResults)
            );

        } else {
            log.info("Received TaskEvent: " + taskEvent);
        }
    }
}
