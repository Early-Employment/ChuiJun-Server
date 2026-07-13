package team.joup.chuijun.domain.submission.service

import jakarta.persistence.EntityManager
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import team.joup.chuijun.domain.problem.repository.ProblemJpaRepository
import team.joup.chuijun.domain.submission.dto.request.SubmitProblemRequest
import team.joup.chuijun.domain.submission.dto.response.SubmitProblemResponse
import team.joup.chuijun.domain.submission.entity.JudgeStatus
import team.joup.chuijun.domain.submission.entity.SubmissionJpaEntity
import team.joup.chuijun.domain.submission.repository.SubmissionJpaRepository
import java.util.NoSuchElementException

@Service
@Transactional(readOnly = true)
class SubmissionService(
    private val submissionJpaRepository: SubmissionJpaRepository,
    private val memberJpaRepository: MemberJpaRepository,
    private val problemJpaRepository: ProblemJpaRepository,
    private val entityManager: EntityManager
) {

    @Transactional
    fun submitProblem(memberId: Long, request: SubmitProblemRequest): SubmitProblemResponse {
        val member = memberJpaRepository.findByIdWithPessimisticLock(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        val problem = problemJpaRepository.findByIdOrNull(request.problemId)
            ?: throw NoSuchElementException("존재하지 않는 문제입니다. ID: ${request.problemId}")

        val submission = SubmissionJpaEntity(
            member = member,
            problem = problem,
            languageCode = request.languageCode,
            submittedCode = request.submittedCode,
            studySeconds = request.studySeconds
        )

        val previousMaxScore = submissionJpaRepository.findMaxScoreByMemberIdAndProblemId(memberId, request.problemId) ?: 0

        submission.completeJudge(request.judgeStatus, request.score)
        val savedSubmission = submissionJpaRepository.save(submission)

        problem.increaseSubmitCount()

        val isSuccess = request.judgeStatus == JudgeStatus.PASSED || request.judgeStatus == JudgeStatus.AC
        if (isSuccess) {
            problem.increaseAcceptedCount()
        }

        var finalRating = member.rating
        if (request.score > previousMaxScore) {
            val scoreGap = request.score - previousMaxScore
            memberJpaRepository.updateRating(memberId, scoreGap)
            finalRating += scoreGap
        }

        return SubmitProblemResponse(
            submissionId = checkNotNull(savedSubmission.id),
            judgeStatus = savedSubmission.judgeStatus,
            score = savedSubmission.score,
            totalScoreAfter = finalRating
        )
    }
}
