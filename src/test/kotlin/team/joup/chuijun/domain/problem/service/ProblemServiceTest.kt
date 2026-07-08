package team.joup.chuijun.domain.problem.service

import org.junit.jupiter.api.Test
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import team.joup.chuijun.domain.problem.entity.AlgorithmType
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity
import team.joup.chuijun.domain.problem.entity.ProblemLevel
import team.joup.chuijun.domain.problem.entity.ProblemStatus
import team.joup.chuijun.domain.problem.entity.SolveStatus
import team.joup.chuijun.domain.problem.repository.ProblemJpaRepository
import team.joup.chuijun.domain.problem.repository.TestCaseJpaRepository
import team.joup.chuijun.domain.submission.entity.JudgeStatus
import team.joup.chuijun.domain.submission.repository.ProblemSubmissionStatusProjection
import team.joup.chuijun.domain.submission.repository.SubmissionJpaRepository
import kotlin.test.assertEquals

class ProblemServiceTest {

    private val problemJpaRepository = mock(ProblemJpaRepository::class.java)
    private val testCaseJpaRepository = mock(TestCaseJpaRepository::class.java)
    private val submissionJpaRepository = mock(SubmissionJpaRepository::class.java)
    private val problemService = ProblemService(
        problemJpaRepository = problemJpaRepository,
        testCaseJpaRepository = testCaseJpaRepository,
        submissionJpaRepository = submissionJpaRepository
    )

    @Test
    fun `filters problem list by clicked level solve status and algorithm type`() {
        val pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"))
        val problem = problem(
            id = 1L,
            title = "서현이의 디자인 입문기",
            level = ProblemLevel.LEVEL_2,
            algorithmType = AlgorithmType.DP
        )

        `when`(
            problemJpaRepository.findUnsolvedFiltered(
                "서현",
                ProblemLevel.LEVEL_2,
                AlgorithmType.DP,
                AlgorithmType.DP.tagNames(),
                1L,
                pageable
            )
        ).thenReturn(PageImpl(listOf(problem), pageable, 1))
        `when`(
            submissionJpaRepository.findJudgeStatusesByMemberIdAndProblemIds(1L, listOf(1L))
        ).thenReturn(emptyList())

        val result = problemService.getProblemList(
            keyword = " 서현 ",
            level = ProblemLevel.LEVEL_2,
            solveStatus = SolveStatus.UNSOLVED,
            algorithmType = AlgorithmType.DP,
            memberId = 1L,
            pageable = pageable
        )

        assertEquals(1, result.totalElements)
        assertEquals("서현이의 디자인 입문기", result.content[0].title)
        assertEquals(ProblemLevel.LEVEL_2, result.content[0].level)
        assertEquals(AlgorithmType.DP, result.content[0].algorithmType)
        assertEquals(SolveStatus.UNSOLVED, result.content[0].solveStatus)
        verify(problemJpaRepository).findUnsolvedFiltered(
            "서현",
            ProblemLevel.LEVEL_2,
            AlgorithmType.DP,
            AlgorithmType.DP.tagNames(),
            1L,
            pageable
        )
    }


    @Test
    fun `parses dropdown labels used by the screen`() {
        assertEquals(ProblemLevel.LEVEL_2, ProblemLevel.fromQuery("Level 2"))
        assertEquals(SolveStatus.UNSOLVED, SolveStatus.fromQuery("안 푼 문제"))
        assertEquals(AlgorithmType.BRUTE_FORCE, AlgorithmType.fromQuery("브루트포스"))
        assertEquals(AlgorithmType.BINARY_SEARCH, AlgorithmType.fromQuery("이분탐색"))
    }


    @Test
    fun `algorithm tag names include raw uppercase and normalized aliases`() {
        val bruteForceTags = AlgorithmType.BRUTE_FORCE.tagNames()
        assertEquals(true, "BRUTE_FORCE" in bruteForceTags)
        assertEquals(true, "BRUTEFORCE" in bruteForceTags)

        val binarySearchTags = AlgorithmType.BINARY_SEARCH.tagNames()
        assertEquals(true, "BINARY_SEARCH" in binarySearchTags)
        assertEquals(true, "BINARYSEARCH" in binarySearchTags)
    }

    @Test
    fun `marks each problem solve status for logged in member`() {
        val pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "id"))
        val solvedProblem = problem(1L, "DP 문제", ProblemLevel.LEVEL_2, AlgorithmType.DP)
        val unsolvedProblem = problem(2L, "BFS 문제", ProblemLevel.LEVEL_3, AlgorithmType.BFS)

        `when`(
            problemJpaRepository.findFiltered(null, null, null, listOf("__NO_ALGORITHM_TAG_FILTER__"), pageable)
        ).thenReturn(PageImpl(listOf(solvedProblem, unsolvedProblem), pageable, 2))
        `when`(
            submissionJpaRepository.findJudgeStatusesByMemberIdAndProblemIds(1L, listOf(1L, 2L))
        ).thenReturn(listOf(SubmissionStatus(1L, JudgeStatus.AC)))

        val result = problemService.getProblemList(
            keyword = null,
            level = null,
            solveStatus = null,
            algorithmType = null,
            memberId = 1L,
            pageable = pageable
        )

        assertEquals(SolveStatus.SOLVED, result.content[0].solveStatus)
        assertEquals(SolveStatus.UNSOLVED, result.content[1].solveStatus)
    }

    private fun problem(
        id: Long,
        title: String,
        level: ProblemLevel,
        algorithmType: AlgorithmType
    ): ProblemJpaEntity {
        return ProblemJpaEntity(
            id = id,
            problemCode = "P$id",
            title = title,
            descriptionMd = "description",
            inputMd = null,
            outputMd = null,
            level = level,
            algorithmType = algorithmType,
            primaryTag = algorithmType.name,
            tagListJson = null,
            status = ProblemStatus.PUBLISHED
        )
    }

    private data class SubmissionStatus(
        override val problemId: Long,
        override val judgeStatus: JudgeStatus
    ) : ProblemSubmissionStatusProjection
}
