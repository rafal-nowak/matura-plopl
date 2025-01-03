package pl.lodz.p.liceum.matura.domain;

public interface TaskExecutor {

    TestResult executeSubtask(Subtask subtask);
}