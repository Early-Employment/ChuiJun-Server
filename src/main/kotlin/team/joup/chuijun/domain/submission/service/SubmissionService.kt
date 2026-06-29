package team.joup.chuijun.domain.submission.service

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
    private val problemJpaRepository: ProblemJpaRepository
) {

    @Transactional
    fun submitProblem(request: SubmitProblemRequest): SubmitProblemResponse {
        val member = memberJpaRepository.findByIdOrNull(request.memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: ${request.memberId}")

        val problem = problemJpaRepository.findByIdOrNull(request.problemId)
            ?: throw NoSuchElementException("존재하지 않는 문제입니다. ID: ${request.problemId}")

        val submission = SubmissionJpaEntity(
            member = member,
            problem = problem,
            languageCode = request.languageCode,
            submittedCode = request.submittedCode,
            studySeconds = request.studySeconds
        )

        val isAlreadyPassed = submissionJpaRepository.existsPassedSubmission(member.id!!, problem.id!!)

        submission.completeJudge(request.judgeStatus, request.score)
        val savedSubmission = submissionJpaRepository.save(submission)

        problem.increaseSubmitCount()

        if (request.judgeStatus == JudgeStatus.PASSED || request.judgeStatus == JudgeStatus.AC) {
            problem.increaseAcceptedCount()
            if (!isAlreadyPassed) {
                member.rating += problem.point
            }
        }

        return SubmitProblemResponse(
            submissionId = checkNotNull(savedSubmission.id),
            judgeStatus = savedSubmission.judgeStatus,
            score = savedSubmission.score,
            totalScoreAfter = member.rating
        )
    }
}
