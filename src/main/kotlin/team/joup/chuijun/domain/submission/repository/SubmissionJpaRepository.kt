package team.joup.chuijun.domain.submission.repository

import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.submission.entity.JudgeStatus
import team.joup.chuijun.domain.submission.entity.SubmissionJpaEntity

interface SubmissionJpaRepository : JpaRepository<SubmissionJpaEntity, Long> {

    @Query("SELECT s FROM SubmissionJpaEntity s LEFT JOIN FETCH s.problem WHERE s.member.id = :memberId")
    fun findByMemberId(
        @Param("memberId") memberId: Long,
        pageable: Pageable
    ): List<SubmissionJpaEntity>

    fun existsByMemberIdAndProblemIdAndJudgeStatusIn(
        memberId: Long,
        problemId: Long,
        judgeStatuses: Collection<JudgeStatus>
    ): Boolean

    @Query(
        """
            SELECT s.problem.id AS problemId, s.judgeStatus AS judgeStatus
            FROM SubmissionJpaEntity s
            WHERE s.member.id = :memberId
              AND s.problem.id IN :problemIds
        """
    )
    fun findJudgeStatusesByMemberIdAndProblemIds(
        @Param("memberId") memberId: Long,
        @Param("problemIds") problemIds: Collection<Long>
    ): List<ProblemSubmissionStatusProjection>

    @Query("SELECT MAX(s.score) FROM SubmissionJpaEntity s WHERE s.member.id = :memberId AND s.problem.id = :problemId")
    fun findMaxScoreByMemberIdAndProblemId(
        @Param("memberId") memberId: Long,
        @Param("problemId") problemId: Long
    ): Int?

    @Query("SELECT s FROM SubmissionJpaEntity s JOIN FETCH s.member JOIN FETCH s.problem WHERE s.member.id IN :memberIds AND s.problem.id IN :problemIds")
    fun findByMemberIdInAndProblemIdIn(
        @Param("memberIds") memberIds: Collection<Long>,
        @Param("problemIds") problemIds: Collection<Long>
    ): List<SubmissionJpaEntity>

    @Query("SELECT s.problem.id FROM SubmissionJpaEntity s WHERE s.member.id = :memberId")
    fun findSubmittedProblemIdsByMemberId(@Param("memberId") memberId: Long): List<Long>
}

interface ProblemSubmissionStatusProjection {
    val problemId: Long
    val judgeStatus: JudgeStatus
}
