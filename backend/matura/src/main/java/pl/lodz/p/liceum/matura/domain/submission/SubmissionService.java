package pl.lodz.p.liceum.matura.domain.submission;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SubmissionService {
    private final SubmissionRepository submissionRepository;

    public Submission save(Submission submission) {
        return submissionRepository.save(submission);
    }

    public void update(Submission submission) {
        submissionRepository.update(submission);
    }

    public void removeById(Integer id) {
        submissionRepository.remove(id);
    }

    public Submission findById(Integer id) {
        return submissionRepository
                .findById(id)
                .orElseThrow(SubmissionNotFoundException::new);
    }
}
