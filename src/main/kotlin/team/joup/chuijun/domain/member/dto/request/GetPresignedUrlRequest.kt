package team.joup.chuijun.domain.member.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "Presigned URL 발급 요청")
data class GetPresignedUrlRequest(
    @Schema(description = "파일 이름 (확장자 포함)", example = "my_profile.png")
    @field:NotBlank(message = "파일명은 필수입니다.")
    val fileName: String
)
