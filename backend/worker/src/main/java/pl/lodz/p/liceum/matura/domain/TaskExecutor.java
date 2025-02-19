package pl.lodz.p.liceum.matura.domain;

import java.util.List;

public interface TaskExecutor {

    List<TestResult> executeSubtask(Subtask subtask);
}