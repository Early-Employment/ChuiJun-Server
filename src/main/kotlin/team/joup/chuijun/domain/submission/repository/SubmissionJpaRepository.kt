package team.joup.chuijun.domain.submission.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.submission.entity.SubmissionJpaEntity

interface SubmissionJpaRepository : JpaRepository<SubmissionJpaEntity, Long> {

    @Query("SELECT s FROM SubmissionJpaEntity s LEFT JOIN FETCH s.problem WHERE s.member.id = :memberId")
    fun findByMemberId(@Param("memberId") memberId: Long, pageable: Pageable): List<SubmissionJpaEntity>

    @Query("select count(s) > 0 from SubmissionJpaEntity s where s.member.id = :memberId and s.problem.id = :problemId and s.judgeStatus = 'PASSED'")
    fun existsPassedSubmission(
        @Param("memberId") memberId: Long,
        @Param("problemId") problemId: Long
    ): Boolean
}

