package kr.co.conceptbe.writing.presentation;

import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import kr.co.conceptbe.writing.app.WritingService;
import kr.co.conceptbe.writing.app.dto.WritingResponses;
import kr.co.conceptbe.writing.presentation.swagger.WritingSwagger;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.apache.http.HttpHeaders.AUTHORIZATION;

@RestController
@RequiredArgsConstructor
@RequestMapping("/writing")
@SecurityRequirement(name = AUTHORIZATION)
public class WritingController implements WritingSwagger {

    private final WritingService writingService;

    @GetMapping
    public ResponseEntity<WritingResponses> getWritings() {
        WritingResponses response = writingService.getWritings();

        return ResponseEntity.ok(response);
    }

}
