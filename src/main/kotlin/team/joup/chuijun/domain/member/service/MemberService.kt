package team.joup.chuijun.domain.member.service

import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.member.dto.response.GetDailyGrassDto
import team.joup.chuijun.domain.member.dto.response.GetMemberProfileResponse
import team.joup.chuijun.domain.member.dto.response.GetRecentActivityDto
import team.joup.chuijun.domain.member.repository.DailyStudyStatsJpaRepository
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.submission.repository.SubmissionJpaRepository
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberJpaRepository: MemberJpaRepository,
    private val dailyStudyStatsJpaRepository: DailyStudyStatsJpaRepository,
    private val submissionJpaRepository: SubmissionJpaRepository
) {
    fun getMemberProfile(memberId: Long): GetMemberProfileResponse {
        val member = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        val oneYearAgo = LocalDate.now().minusYears(1)
        val statsList = dailyStudyStatsJpaRepository.findStatsSince(memberId, oneYearAgo)

        val grassRecords = statsList.map { stats ->
            GetDailyGrassDto(
                date = stats.id.studyDate,
                solvedCount = stats.solvedCount,
                studySeconds = stats.studySeconds
            )
        }

        val totalSolved = dailyStudyStatsJpaRepository.countTotalSolvedByMemberId(memberId) ?: 0

        val pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "submittedAt"))
        val recentSubmissions = submissionJpaRepository.findByMemberId(memberId, pageable)

        val recentActivities = recentSubmissions.map { submission ->
            GetRecentActivityDto(
                submissionId = submission.id ?: throw IllegalStateException("제출 데이터의 식별자(ID)가 누락되었습니다."),
                problemTitle = submission.problem?.title ?: "삭제된 문제",
                problemLevel = submission.problem?.level ?: 0,
                score = submission.score,
                submittedAt = submission.submittedAt
            )
        }

        return GetMemberProfileResponse(
            memberId = member.id ?: throw IllegalStateException("회원 데이터의 식별자(ID)가 누락되었습니다."),
            name = member.name,
            profileImageUrl = member.profileImageUrl,
            tier = member.tier,
            rating = member.rating,
            coin = member.coin,
            currentStreak = member.currentStreak,
            totalSolvedCount = totalSolved,
            grassRecord = grassRecords,
            recentActivities = recentActivities
        )
    }
}
