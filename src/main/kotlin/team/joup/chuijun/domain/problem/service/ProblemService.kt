package team.joup.chuijun.domain.problem.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.problem.dto.response.GetProblemDetailResponse
import team.joup.chuijun.domain.problem.dto.response.GetProblemListResponse
import team.joup.chuijun.domain.problem.dto.response.TestCaseDto
import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.repository.ProblemJpaRepository
import team.joup.chuijun.domain.problem.repository.TestCaseJpaRepository

@Service
@Transactional(readOnly = true)
class ProblemService(
    private val problemJpaRepository: ProblemJpaRepository,
    private val testCaseJpaRepository: TestCaseJpaRepository
) {

    fun getProblemList(pageable: Pageable): Page<GetProblemListResponse> {
        val problems = problemJpaRepository.findAll(pageable)
        return problems.map { problem ->
            GetProblemListResponse(
                problemId = problem.id ?: throw IllegalStateException("문제 데이터의 식별자가 누락되었습니다."),
                problemCode = problem.problemCode,
                title = problem.title,
                level = problem.level,
                primaryTag = problem.primaryTag,
                point = problem.point
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
                testCaseId = testCase.id ?: throw IllegalStateException("테스트 케이스 데이터의 식별자가 누락되었습니다."),
                caseType = testCase.caseType.name,
                inputText = testCase.inputText,
                expectedOutputText = testCase.expectedOutputText,
                explanationMd = testCase.explanationMd
            )
        }

        return GetProblemDetailResponse(
            problemId = problem.id ?: throw IllegalStateException("문제 데이터의 식별자가 누락되었습니다."),
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
            testCases = testCases
        )
    }
}
