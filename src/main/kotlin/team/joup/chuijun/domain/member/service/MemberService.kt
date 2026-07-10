package team.joup.chuijun.domain.member.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import software.amazon.awssdk.services.s3.model.PutObjectRequest
import software.amazon.awssdk.services.s3.presigner.S3Presigner
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest
import team.joup.chuijun.domain.member.dto.response.GetMemberRankingResponse
import team.joup.chuijun.domain.member.dto.response.GetMemberProfileResponse
import team.joup.chuijun.domain.member.dto.response.GetDailyGrassDto
import team.joup.chuijun.domain.member.dto.response.GetRecentActivityDto
import team.joup.chuijun.domain.member.dto.response.PresignedUrlResponse
import team.joup.chuijun.domain.member.entity.MemberJpaEntity
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.member.repository.DailyStudyStatsJpaRepository
import team.joup.chuijun.domain.submission.repository.SubmissionJpaRepository
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.NoSuchElementException
import java.util.UUID

@Service
@Transactional(readOnly = true)
class MemberService(
    private val memberJpaRepository: MemberJpaRepository,
    private val dailyStudyStatsJpaRepository: DailyStudyStatsJpaRepository,
    private val submissionJpaRepository: SubmissionJpaRepository,
    private val s3Presigner: S3Presigner,

    @Value("\${aws.s3.bucket-name}")
    private val bucketName: String,

    @Value("\${aws.s3.region-domain}")
    private val regionDomain: String
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
        return convertToProfileResponse(member)
    }

    fun getMemberProfileByEmail(email: String): GetMemberProfileResponse {
        val member = memberJpaRepository.findByEmail(email)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. Email: $email")
        return convertToProfileResponse(member)
    }

    @Transactional
    fun updateProfileImage(memberId: Long, profileImageUrl: String?) {
        val member = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        member.profileImageUrl = profileImageUrl?.takeIf { it.isNotBlank() }
        member.updatedAt = LocalDateTime.now()
    }

    fun createProfileImagePresignedUrl(memberId: Long, fileName: String): PresignedUrlResponse {
        if (!memberJpaRepository.existsById(memberId)) {
            throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")
        }

        val fileExtension = fileName.substringAfterLast('.', "").lowercase()
        if (fileExtension.isBlank()) {
            throw IllegalArgumentException("파일 확장자가 필요합니다.")
        }

        val contentType = getProfileContentType(fileExtension)
        val uniqueFileName = "profiles/${memberId}_${UUID.randomUUID()}.${fileExtension}"

        val objectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(uniqueFileName)
            .contentType(contentType)
            .build()

        val presignedRequest = PutObjectPresignRequest.builder()
            .signatureDuration(Duration.ofMinutes(10))
            .putObjectRequest(objectRequest)
            .build()

        val presignedUrl = s3Presigner.presignPutObject(presignedRequest).url().toString()
        val finalProfileImageUrl = "https://$bucketName.$regionDomain/$uniqueFileName"

        return PresignedUrlResponse(
            presignedUrl = presignedUrl,
            profileImageUrl = finalProfileImageUrl
        )
    }

    private fun getProfileContentType(extension: String): String {
        return when (extension) {
            "png" -> "image/png"
            "gif" -> "image/gif"
            "webp" -> "image/webp"
            "jpg", "jpeg" -> "image/jpeg"
            else -> throw IllegalArgumentException("지원하지 않는 이미지 확장자입니다: $extension")
        }
    }

    private fun convertToProfileResponse(member: MemberJpaEntity): GetMemberProfileResponse {
        val memberId = checkNotNull(member.id) { "회원 데이터의 식별자가 누락되었습니다." }
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
                problemLevel = submission.problem?.level?.ordinal ?: 0,
                score = submission.score,
                submittedAt = submission.submittedAt
            )
        }

        return GetMemberProfileResponse(
            memberId = memberId,
            name = member.name,
            profileImageUrl = member.profileImageUrl,
            tier = member.tier,
            rating = member.rating,
            grade = member.grade,
            classNum = member.classNum,
            number = member.number,
            currentStreak = member.currentStreak,
            totalSolvedCount = totalSolvedCount.toInt(),
            grassRecord = grassRecord,
            recentActivities = recentActivities
        )
    }
}
