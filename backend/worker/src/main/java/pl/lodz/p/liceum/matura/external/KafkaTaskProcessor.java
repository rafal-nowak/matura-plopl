package pl.lodz.p.liceum.matura.external;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import pl.lodz.p.liceum.matura.config.KafkaProperties;
import pl.lodz.p.liceum.matura.domain.*;
import pl.lodz.p.liceum.matura.external.worker.task.events.*;

import java.util.List;
import java.util.concurrent.*;

@Log
@Service
@RequiredArgsConstructor
public class KafkaTaskProcessor {

    private final KafkaProperties kafkaProperties;
    private final KafkaTemplate<String, TaskEvent> kafkaTemplate;
    private final TaskExecutor taskExecutor;
    private final KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    // Maksymalna liczba jednoczesnych zadań
    private static final int MAX_THREADS = 5;
    private final ExecutorService taskExecutorService = new ThreadPoolExecutor(
            MAX_THREADS,
            MAX_THREADS,
            60L,
            TimeUnit.SECONDS,
            new LinkedBlockingQueue<>()
    );

    @KafkaListener(
            id = "taskListener",
            topics = "#{kafkaProperties.commandTopic}",
            groupId = "#{kafkaProperties.groupId}",
            containerFactory = "taskKafkaListenerFactory",
            autoStartup = "false" // Startujemy ręcznie
    )
    public void onReceive(TaskEvent taskEvent) {
        taskExecutorService.submit(() -> processTask(taskEvent));

        if (!hasAvailableThreads()) {
            log.info("🚦 No available threads, attempting to pause Kafka Listener...");
            pauseKafkaListener();
        }
    }

//    private void processTask(TaskEvent taskEvent) {
//        try {
//            if (taskEvent instanceof TaskSentForProcessingEvent taskSentForProcessingEvent) {
//                log.info("Processing TaskSentForProcessingEvent: " + taskEvent);
//
//                Task task = new Task(taskEvent.getTaskId(), taskEvent.getWorkspaceUrl());
//
//                for (int i = 1; i <= taskSentForProcessingEvent.getNumberOfSubtasks(); i++) {
//                    Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), i, TestType.FULL);
//                    List<TestResult> testResults = taskExecutor.executeSubtask(subtask);
//
//                    kafkaTemplate.send(
//                            kafkaProperties.getReportTopic(),
//                            new SubtaskFullProcessingCompleteEvent(
//                                    taskEvent.getTaskId(),
//                                    taskSentForProcessingEvent.getSubmissionId(),
//                                    subtask.getWorkspaceUrl(),
//                                    subtask.getNumber(),
//                                    testResults
//                            )
//                    );
//                }
//
//                kafkaTemplate.send(
//                        kafkaProperties.getReportTopic(),
//                        new TaskProcessingCompleteEvent(
//                                task.getTaskId(),
//                                taskSentForProcessingEvent.getSubmissionId(),
//                                task.getWorkspaceUrl()
//                        )
//                );
//
//            } else if (taskEvent instanceof SubtaskSentForFastProcessingEvent subtaskSentForFastProcessingEvent) {
//                log.info("Processing SubtaskSentForFastProcessingEvent: " + taskEvent);
//                Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), subtaskSentForFastProcessingEvent.getNumber(), TestType.FAST);
//                List<TestResult> testResults = taskExecutor.executeSubtask(subtask);
//
//                kafkaTemplate.send(
//                        kafkaProperties.getReportTopic(),
//                        new SubtaskFastProcessingCompleteEvent(
//                                taskEvent.getTaskId(),
//                                subtaskSentForFastProcessingEvent.getSubmissionId(),
//                                subtask.getWorkspaceUrl(),
//                                subtask.getNumber(),
//                                testResults
//                        )
//                );
//
//            } else if (taskEvent instanceof SubtaskSentForFullProcessingEvent subtaskSentForFullProcessingEvent) {
//                log.info("Processing SubtaskSentForFullProcessingEvent: " + taskEvent);
//                Subtask subtask = new Subtask(taskEvent.getWorkspaceUrl(), subtaskSentForFullProcessingEvent.getNumber(), TestType.FULL);
//                List<TestResult> testResults = taskExecutor.executeSubtask(subtask);
//
//                kafkaTemplate.send(
//                        kafkaProperties.getReportTopic(),
//                        new SubtaskFullProcessingCompleteEvent(
//                                taskEvent.getTaskId(),
//                                subtaskSentForFullProcessingEvent.getSubmissionId(),
//                                subtask.getWorkspaceUrl(),
//                                subtask.getNumber(),
//                                testResults
//                        )
//                );
//
//            } else {
//                log.info("Received TaskEvent: " + taskEvent);
//            }
//        } finally {
//            // Po zakończeniu zadania sprawdzamy, czy można wznowić nasłuchiwanie
//            if (hasAvailableThreads()) {
//                resumeKafkaListener();
//            }
//        }
//    }

    private void processTask(TaskEvent taskEvent) {
        try {
            log.info("🔄 Processing TaskEvent: " + taskEvent);

            // Symulacja długiego procesu
            Thread.sleep(5000);

            log.info("✅ Finished Processing TaskEvent: " + taskEvent);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warning("⚠️ Task interrupted: " + e.getMessage());
        } finally {
            if (hasAvailableThreads()) {
                resumeKafkaListener();
            }
        }
    }


    private boolean hasAvailableThreads() {
        ThreadPoolExecutor executor = (ThreadPoolExecutor) taskExecutorService;
        int activeThreads = executor.getActiveCount();
        log.info("Active Threads: " + activeThreads + "/" + MAX_THREADS);
        return activeThreads < MAX_THREADS;
    }


    private void pauseKafkaListener() {
        var container = kafkaListenerEndpointRegistry.getListenerContainer("taskListener");

        if (container == null) {
            log.severe("❌ Kafka Listener Container is NULL! It was not registered properly.");
            return;
        }

        if (container.isRunning()) {
            log.info("⏸️ Pausing Kafka Listener (no available threads)");
            container.stop();
        } else {
            log.warning("⚠️ Kafka Listener is already paused.");
        }
    }


    private void resumeKafkaListener() {
        var container = kafkaListenerEndpointRegistry.getListenerContainer("taskListener");

        if (container == null) {
            log.severe("❌ Kafka Listener Container is NULL! It was not registered properly.");
            return;
        }

        if (!container.isRunning()) {
            log.info("▶️ Resuming Kafka Listener (threads available)");
            container.start();
        } else {
            log.info("🟢 Kafka Listener already running.");
        }
    }

    @Bean
    public ApplicationRunner kafkaStartupRunner() {
        return args -> {
            log.info("Starting Kafka Listener...");
            var container = kafkaListenerEndpointRegistry.getListenerContainer("taskListener");
            if (container != null && !container.isRunning()) {
                container.start();
                log.info("Kafka Listener started successfully.");
            } else {
                if (container == null) {
                    log.severe("❌ Kafka Listener not found!");
                }
                log.info("🟢 Kafka Listener already running.");
            }
        };
    }

    @Bean
    public ApplicationRunner runner(KafkaListenerEndpointRegistry registry) {
        return args -> {
            log.info("📌 Available Kafka Listeners: " + kafkaListenerEndpointRegistry.getListenerContainerIds());

        };
    }

}
