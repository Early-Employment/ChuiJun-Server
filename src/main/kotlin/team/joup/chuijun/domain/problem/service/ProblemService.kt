package team.joup.chuijun.domain.problem.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.problem.dto.request.CreateProblemRequest
import team.joup.chuijun.domain.problem.dto.request.UpdateProblemRequest
import team.joup.chuijun.domain.problem.dto.response.GetProblemDetailResponse
import team.joup.chuijun.domain.problem.dto.response.GetProblemListResponse
import team.joup.chuijun.domain.problem.dto.response.TestCaseDto
import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity
import team.joup.chuijun.domain.problem.entity.TestCaseJpaEntity
import team.joup.chuijun.domain.problem.repository.ProblemJpaRepository
import team.joup.chuijun.domain.problem.repository.TestCaseJpaRepository
import java.time.LocalDateTime

@Service
@Transactional(readOnly = true)
class ProblemService(
    private val problemJpaRepository: ProblemJpaRepository,
    private val testCaseJpaRepository: TestCaseJpaRepository
) {

    fun getProblemList(keyword: String?, pageable: Pageable): Page<GetProblemListResponse> {
        val problems = if (!keyword.isNullOrBlank()) {
            problemJpaRepository.findByTitleContaining(keyword, pageable)
        } else {
            problemJpaRepository.findAll(pageable)
        }

        return problems.map { problem ->
            GetProblemListResponse(
                problemId = checkNotNull(problem.id) { "문제 데이터의 식별자가 누락되었습니다." },
                problemCode = problem.problemCode,
                title = problem.title,
                level = problem.level,
                primaryTag = problem.primaryTag,
                point = problem.point,
                acceptRate = problem.getAcceptRate()
            )
        }
    }

    fun getProblemDetail(problemId: Long): GetProblemDetailResponse {
        val problem = problemJpaRepository.findByIdOrNull(problemId)
            ?: throw NoSuchElementException("존재하지 않는 문제입니다. ID: $problemId")

        val testCases = testCaseJpaRepository.findByProblemIdAndCaseTypeAndIsEnabledTrueOrderBySortOrderAsc(
            problemId,
            CaseType.PUBLIC
        ).map { testCase ->
            TestCaseDto(
                testCaseId = checkNotNull(testCase.id) { "테스트 케이스 데이터의 식별자가 누락되었습니다." },
                caseType = testCase.caseType.name,
                inputText = testCase.inputText,
                expectedOutputText = testCase.expectedOutputText,
                explanationMd = testCase.explanationMd
            )
        }

        return GetProblemDetailResponse(
            problemId = checkNotNull(problem.id) { "문제 데이터의 식별자가 누락되었습니다." },
            problemCode = problem.problemCode,
            title = problem.title,
            descriptionMd = problem.descriptionMd,
            inputMd = problem.inputMd,
            outputMd = problem.outputMd,
            level = problem.level,
            primaryTag = problem.primaryTag,
            point = problem.point,
            timeLimitMs = problem.timeLimitMs,
            memoryLimitKb = problem.memoryLimitKb,
            testCases = testCases,
            acceptRate = problem.getAcceptRate()
        )
    }

    @Transactional // 👈 리뷰 반영: 명시적 쓰기 트랜잭션을 부여하여 원자적 처리 보장
    fun createProblem(request: CreateProblemRequest): Long {
        val problem = ProblemJpaEntity(
            problemCode = request.problemCode,
            title = request.title,
            descriptionMd = request.descriptionMd,
            inputMd = request.inputMd,
            outputMd = request.outputMd,
            level = request.level,
            primaryTag = request.primaryTag,
            tagListJson = request.tagListJson,
            point = request.point,
            timeLimitMs = request.timeLimitMs,
            memoryLimitKb = request.memoryLimitKb,
            status = request.status
        )
        val savedProblem = problemJpaRepository.save(problem)

        val testCases = request.testCases.map { tcRequest ->
            TestCaseJpaEntity(
                problem = savedProblem,
                caseType = tcRequest.caseType,
                inputText = tcRequest.inputText,
                expectedOutputText = tcRequest.expectedOutputText,
                explanationMd = tcRequest.explanationMd,
                sortOrder = tcRequest.sortOrder,
                isEnabled = true
            )
        }
        testCaseJpaRepository.saveAll(testCases)

        return checkNotNull(savedProblem.id)
    }

    @Transactional // 👈 리뷰 반영: 쓰기 트랜잭션 처리
    fun updateProblem(problemId: Long, request: UpdateProblemRequest) {
        val problem = problemJpaRepository.findByIdOrNull(problemId)
            ?: throw NoSuchElementException("존재하지 않는 문제입니다. ID: $problemId")

        problem.title = request.title
        problem.descriptionMd = request.descriptionMd
        problem.inputMd = request.inputMd
        problem.outputMd = request.outputMd
        problem.level = request.level
        problem.primaryTag = request.primaryTag
        problem.tagListJson = request.tagListJson
        problem.point = request.point
        problem.timeLimitMs = request.timeLimitMs
        problem.memoryLimitKb = request.memoryLimitKb
        problem.status = request.status
        problem.updatedAt = LocalDateTime.now()

        testCaseJpaRepository.deleteByProblemId(problemId)

        val newTestCases = request.testCases.map { tcRequest ->
            TestCaseJpaEntity(
                problem = problem,
                caseType = tcRequest.caseType,
                inputText = tcRequest.inputText,
                expectedOutputText = tcRequest.expectedOutputText,
                explanationMd = tcRequest.explanationMd,
                sortOrder = tcRequest.sortOrder,
                isEnabled = tcRequest.isEnabled
            )
        }
        testCaseJpaRepository.saveAll(newTestCases)
    }

    @Transactional // 👈 리뷰 반영: 쓰기 트랜잭션 처리
    fun deleteProblem(problemId: Long) {
        val problem = problemJpaRepository.findByIdOrNull(problemId)
            ?: throw NoSuchElementException("존재하지 않는 문제입니다. ID: $problemId")

        testCaseJpaRepository.deleteByProblemId(problemId)
        problemJpaRepository.delete(problem)
    }
}
