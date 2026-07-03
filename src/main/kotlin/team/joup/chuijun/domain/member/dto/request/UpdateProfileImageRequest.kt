package team.joup.chuijun.domain.member.dto.request

import jakarta.validation.constraints.Size

data class UpdateProfileImageRequest(
    @field:Size(max = 500, message = "프로필 이미지 URL의 길이는 500자를 초과할 수 없습니다.")
    val profileImageUrl: String?
)
