package team.joup.chuijun.domain.problem.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.entity.TestCaseJpaEntity

interface TestCaseJpaRepository : JpaRepository<TestCaseJpaEntity, Long> {
    fun findByProblemIdAndCaseTypeAndIsEnabledTrueOrderBySortOrderAsc(
        problemId: Long,
        caseType: CaseType
    ): List<TestCaseJpaEntity>

    fun deleteByProblemId(problemId: Long)
}
