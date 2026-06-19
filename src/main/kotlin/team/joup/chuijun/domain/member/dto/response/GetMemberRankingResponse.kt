package team.joup.chuijun.domain.member.dto.response

import team.joup.chuijun.domain.member.entity.MemberTier

data class GetMemberRankingResponse(
    val memberId: Long,
    val name: String,
    val profileImageUrl: String?,
    val tier: MemberTier,
    val rating: Int,
    val grade: Int?,
    val classNum: Int?,
    val totalSolvedCount: Long
)
