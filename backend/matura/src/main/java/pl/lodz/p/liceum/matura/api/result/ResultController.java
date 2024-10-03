package pl.lodz.p.liceum.matura.api.result;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.lodz.p.liceum.matura.domain.result.Result;
import pl.lodz.p.liceum.matura.domain.result.ResultService;

@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/api/v1/results")
public class ResultController {

    private final ResultService resultService;
    private final ResultDtoMapper mapper;

    @GetMapping(path = "/{id}")
    public ResponseEntity<ResultDto> getResult(@PathVariable Integer id) {
        Result result = resultService.findById(id);
        return ResponseEntity.ok(mapper.toDto(result));
    }
}
