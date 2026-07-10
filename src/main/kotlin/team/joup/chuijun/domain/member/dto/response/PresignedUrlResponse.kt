package team.joup.chuijun.domain.member.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "Presigned URL 발급 응답")
data class PresignedUrlResponse(
    @Schema(description = "S3 임시 파일 업로드 URL (프론트엔드가 직접 PUT 요청할 주소)")
    val presignedUrl: String,

    @Schema(description = "업로드 완료 후 최종적으로 DB에 저장할 프로필 이미지 URL")
    val profileImageUrl: String
)
