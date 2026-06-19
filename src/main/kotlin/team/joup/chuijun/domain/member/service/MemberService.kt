package team.joup.chuijun.domain.member.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.member.dto.response.GetMemberRankingResponse
import team.joup.chuijun.domain.member.dto.response.GetMemberProfileResponse
import team.joup.chuijun.domain.member.dto.response.GetDailyGrassDto
import team.joup.chuijun.domain.member.dto.response.GetRecentActivityDto
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.member.repository.DailyStudyStatsJpaRepository
import team.joup.chuijun.domain.submission.repository.SubmissionJpaRepository
import java.time.LocalDate
import java.time.ZoneId
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberJpaRepository: MemberJpaRepository,
    private val dailyStudyStatsJpaRepository: DailyStudyStatsJpaRepository,
    private val submissionJpaRepository: SubmissionJpaRepository
) {

    fun getRankings(pageable: Pageable): Page<GetMemberRankingResponse> {
        val members = memberJpaRepository.findAll(pageable)
        return members.map { member ->
            GetMemberRankingResponse(
                memberId = checkNotNull(member.id) { "회원 데이터의 식별자가 누락되었습니다." },
                name = member.name,
                profileImageUrl = member.profileImageUrl,
                tier = member.tier,
                rating = member.rating,
                grade = member.grade,
                classNum = member.classNum,
                totalSolvedCount = member.totalSolvedCount
            )
        }
    }

    fun getMemberProfile(memberId: Long): GetMemberProfileResponse {
        val member = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        val oneYearAgo = LocalDate.now(ZoneId.of("Asia/Seoul")).minusYears(1)
        val statsList = dailyStudyStatsJpaRepository.findStatsSince(memberId, oneYearAgo)

        val grassRecord = statsList.map { stats ->
            GetDailyGrassDto(
                date = stats.id.studyDate,
                solvedCount = stats.solvedCount,
                studySeconds = stats.studySeconds
            )
        }

        val totalSolvedCount = dailyStudyStatsJpaRepository.countTotalSolvedByMemberId(memberId) ?: 0

        val pageable = PageRequest.of(0, 4, Sort.by(Sort.Direction.DESC, "submittedAt"))
        val recentSubmissions = submissionJpaRepository.findByMemberId(memberId, pageable)

        val recentActivities = recentSubmissions.map { submission ->
            GetRecentActivityDto(
                submissionId = checkNotNull(submission.id) { "제출 데이터의 식별자가 누락되었습니다." },
                problemTitle = submission.problem?.title ?: "삭제된 문제",
                problemLevel = submission.problem?.level ?: 0,
                score = submission.score,
                submittedAt = submission.submittedAt
            )
        }

        return GetMemberProfileResponse(
            memberId = checkNotNull(member.id) { "회원 데이터의 식별자가 누락되었습니다." },
            name = member.name,
            profileImageUrl = member.profileImageUrl,
            tier = member.tier,
            rating = member.rating,
            coin = member.coin,
            currentStreak = member.currentStreak,
            totalSolvedCount = totalSolvedCount,
            grassRecord = grassRecord,
            recentActivities = recentActivities
        )
    }
}
