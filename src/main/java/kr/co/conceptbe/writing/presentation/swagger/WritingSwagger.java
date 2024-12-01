package kr.co.conceptbe.writing.presentation.swagger;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.co.conceptbe.writing.app.dto.WritingResponses;
import org.springframework.http.ResponseEntity;

@Tag(name = "Writing", description = "서비스에 필요한 데이터를 받을 수 있는 API")
public interface WritingSwagger {

    @Operation(summary = "서비스에 필요한 데이터를 받을 수 있는 API")
    ResponseEntity<WritingResponses> getWritings();

}
