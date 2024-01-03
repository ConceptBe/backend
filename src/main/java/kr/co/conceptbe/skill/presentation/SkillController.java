package kr.co.conceptbe.skill.presentation;

import java.util.List;
import kr.co.conceptbe.skill.application.SkillService;
import kr.co.conceptbe.skill.application.dto.FindMainSkillResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {

    private final SkillService skillService;

    @GetMapping("/main")
    ResponseEntity<List<FindMainSkillResponse>> getMainSkills() {
        List<FindMainSkillResponse> responses = skillService.getMainSkills();
        return ResponseEntity.ok(responses);
    }
}
