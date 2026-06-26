package team.joup.chuijun.domain.problem.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.problem.entity.CaseType
import team.joup.chuijun.domain.problem.entity.TestCaseJpaEntity

interface TestCaseJpaRepository : JpaRepository<TestCaseJpaEntity, Long> {

    fun findByProblemIdAndCaseTypeAndIsEnabledTrueOrderBySortOrderAsc(
        problemId: Long,
        caseType: CaseType
    ): List<TestCaseJpaEntity>

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("delete from TestCaseJpaEntity t where t.problem.id = :problemId")
    fun deleteByProblemId(@Param("problemId") problemId: Long)
}
