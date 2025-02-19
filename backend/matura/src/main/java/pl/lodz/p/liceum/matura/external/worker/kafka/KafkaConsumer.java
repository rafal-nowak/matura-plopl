package pl.lodz.p.liceum.matura.external.worker.kafka;

import lombok.AllArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.appservices.TaskApplicationService;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.domain.result.*;
import pl.lodz.p.liceum.matura.domain.task.Task;
import pl.lodz.p.liceum.matura.domain.task.TaskState;
import pl.lodz.p.liceum.matura.domain.template.Template;
import pl.lodz.p.liceum.matura.domain.template.TemplateService;
import pl.lodz.p.liceum.matura.external.worker.task.events.*;

import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.List;


@Log
@AllArgsConstructor
@Service
public class KafkaConsumer {

    private final TaskEventMapper taskEventMapper;
    private final SubtaskResultService subtaskResultService;
    private final TestResultService testResultService;
    private final TemplateService templateService;
    private final TaskApplicationService taskService;
    private final Clock clock;

    private final KafkaProperties kafkaProperties;

    @KafkaListener(topics = "#{kafkaProperties.reportTopic}", groupId = "#{kafkaProperties.groupId}",
            containerFactory = "taskKafkaListenerFactory")
    public void onReceive(TaskEvent taskEvent) {

        if (taskEvent instanceof TaskProcessingCompleteEvent) {
            Task task = taskService.findById(taskEvent.getTaskId());
            Template template = templateService.findById(task.getTemplateId());
            log.info("Processing of task completed successfully at " + task.getWorkspaceUrl());
            List<SubtaskResult> subtaskResults = subtaskResultService.findBySubmissionId(taskEvent.getSubmissionId());
            if (subtaskResults.size() == template.getNumberOfSubtasks() && subtaskResults.stream().allMatch(r -> r.getScore() == 100)) {
                updateTaskState(task, TaskState.FINISHED);
                taskService.deleteWorkspace(task);
            }
            else {
                updateTaskState(task, TaskState.CREATED);
            }
        } else if (taskEvent instanceof SubtaskFastProcessingCompleteEvent event) {
            log.info(String.format("Fast processing of subtask %s completed successfully", event.getNumber()));
            int score = getScore(event.getTestResults());
            log.info(String.format("Score: %d", score));
            SubtaskResult subtaskResult = new SubtaskResult(null, event.getSubmissionId(), event.getNumber(), "", score, ZonedDateTime.now(clock));
            subtaskResult = subtaskResultService.save(subtaskResult);
            for (TestResult testResult : event.getTestResults()) {
                testResult.setSubtaskResultId(subtaskResult.getId());
                testResult.setCreatedAt(ZonedDateTime.now(clock));
                testResultService.save(testResult);
            }
        } else if (taskEvent instanceof SubtaskFullProcessingCompleteEvent event) {
            log.info(String.format("Full processing of subtask %s completed successfully", event.getNumber()));
            int score = getScore(event.getTestResults());
            log.info(String.format("Score: %d", score));
            SubtaskResult subtaskResult = new SubtaskResult(null, event.getSubmissionId(), event.getNumber(), "", score, ZonedDateTime.now(clock));
            subtaskResult = subtaskResultService.save(subtaskResult);
            for (TestResult testResult : event.getTestResults()) {
                testResult.setSubtaskResultId(subtaskResult.getId());
                testResult.setCreatedAt(ZonedDateTime.now(clock));
                testResultService.save(testResult);
            }
        } else if (taskEvent instanceof TaskProcessingFailedEvent) {
            Task task = taskEventMapper.toDomain(taskEvent);
            log.info("Processing of task failed at " + task.getWorkspaceUrl());
        } else if (taskEvent instanceof SubtaskProcessingFailedEvent event) {
            log.info(String.format("Processing of subtask %s failed", event.getNumber()));
        } else {
            log.info("Received taskEvent: " + taskEvent);
        }

    }
    private void updateTaskState(Task task, TaskState state) {
        task.setState(state);
        taskService.update(task);
    }
    private int getScore(List<TestResult> results) {
        return (int) results.stream().filter(r -> r.getVerdict().equals(Verdict.ACCEPTED)).count() * 100 / results.size();
    }
}
