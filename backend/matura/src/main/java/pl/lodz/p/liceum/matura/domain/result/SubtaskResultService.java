package pl.lodz.p.liceum.matura.domain.result;

import lombok.RequiredArgsConstructor;

import java.util.List;

@RequiredArgsConstructor
public class SubtaskResultService {
    private final SubtaskResultRepository subtaskResultRepository;

    public SubtaskResult save(SubtaskResult subtaskResult) {
        return subtaskResultRepository.save(subtaskResult);
    }

    public void update(SubtaskResult subtaskResult) {
        subtaskResultRepository.update(subtaskResult);
    }

    public void removeById(Integer id) {
        subtaskResultRepository.remove(id);
    }

    public SubtaskResult findById(Integer id) {
        return subtaskResultRepository
                .findById(id)
                .orElseThrow(ResultNotFoundException::new);
    }
    public List<SubtaskResult> findBySubmissionId(Integer submissionId) {
        return subtaskResultRepository.findBySubmissionId(submissionId);
    }
}
