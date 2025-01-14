package pl.lodz.p.liceum.matura.api.result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResult;
import pl.lodz.p.liceum.matura.domain.result.SubtaskResultService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/results")
public class SubtaskResultController {

    private final SubtaskResultService subtaskResultService;
    private final SubtaskResultDtoMapper mapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<SubtaskResultDto> getSubtaskResult(@PathVariable Integer id) {
        SubtaskResult subtaskResult = subtaskResultService.findById(id);
        return ResponseEntity.ok(mapper.toDto(subtaskResult));
    }
}
