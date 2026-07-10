package team.joup.chuijun.domain.member.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Presigned URL 발급 응답")
data class PresignedUrlResponse(
    @Schema(description = "S3 임시 파일 업로드 URL")
    val presignedUrl: String,

    @Schema(description = "최종 프로필 이미지 URL")
    val profileImageUrl: String
)
