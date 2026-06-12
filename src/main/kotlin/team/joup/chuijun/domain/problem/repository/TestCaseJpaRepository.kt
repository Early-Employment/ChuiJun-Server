package team.joup.chuijun.domain.problem.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.problem.entity.TestCaseJpaEntity

interface TestCaseJpaRepository : JpaRepository<TestCaseJpaEntity, Long> {
    fun findByProblemIdAndIsEnabledTrueOrderBySortOrderAsc(problemId: Long): List<TestCaseJpaEntity>
}
