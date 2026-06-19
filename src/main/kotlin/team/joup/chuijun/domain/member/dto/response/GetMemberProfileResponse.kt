package team.joup.chuijun.domain.member.dto.response

import io.swagger.v3.oas.annotations.media.Schema
import team.joup.chuijun.domain.member.entity.MemberTier
import java.time.LocalDate
import java.time.LocalDateTime

@Schema(description = "마이페이지 프로필 및 대시보드 통계 조회 응답")
data class GetMemberProfileResponse(
    @Schema(description = "회원 ID", example = "1")
    val memberId: Long,

    @Schema(description = "이름/닉네임", example = "김철수")
    val name: String,

    @Schema(description = "프로필 이미지 URL", example = "https://example.com/profile.png")
    val profileImageUrl: String?,

    @Schema(description = "티어", example = "SILVER")
    val tier: MemberTier,

    @Schema(description = "레이팅 점수", example = "4720")
    val rating: Int,

    @Schema(description = "현재 연속 스트릭 일수", example = "5")
    val currentStreak: Int,

    @Schema(description = "해결한 총 문제 수", example = "47")
    val totalSolvedCount: Int,

    @Schema(description = "일별 학습 통계 (잔디밭 기록)")
    val grassRecord: List<GetDailyGrassDto>,

    @Schema(description = "최근 활동 기록 리스트 (최대 4개)")
    val recentActivities: List<GetRecentActivityDto>
)

@Schema(description = "잔디밭 일별 기록")
data class GetDailyGrassDto(
    @Schema(description = "학습 날짜", example = "2026-06-10")
    val date: LocalDate,

    @Schema(description = "해당 날짜에 해결한 문제 수", example = "3")
    val solvedCount: Int,

    @Schema(description = "해당 날짜 총 공부 시간 (초)", example = "3600")
    val studySeconds: Int
)

@Schema(description = "최근 활동 기록")
data class GetRecentActivityDto(
    @Schema(description = "제출 고유 ID", example = "1024")
    val submissionId: Long,

    @Schema(description = "문제 제목", example = "스택 (Stack)")
    val problemTitle: String,

    @Schema(description = "문제 레벨", example = "2")
    val problemLevel: Byte,

    @Schema(description = "최종 점수", example = "100")
    val score: Int,

    @Schema(description = "제출 일시")
    val submittedAt: LocalDateTime
)
