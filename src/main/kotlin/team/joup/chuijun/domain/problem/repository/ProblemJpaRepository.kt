package team.joup.chuijun.domain.problem.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.problem.entity.AlgorithmType
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity
import team.joup.chuijun.domain.problem.entity.ProblemLevel
import team.joup.chuijun.domain.submission.entity.JudgeStatus

interface ProblemJpaRepository : JpaRepository<ProblemJpaEntity, Long> {

    @Query(
        value = """
            SELECT p FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
        """,
        countQuery = """
            SELECT COUNT(p) FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
        """
    )
    fun findFiltered(
        @Param("keyword") keyword: String?,
        @Param("level") level: ProblemLevel?,
        @Param("algorithmType") algorithmType: AlgorithmType?,
        @Param("algorithmTagNames") algorithmTagNames: Collection<String>,
        pageable: Pageable
    ): Page<ProblemJpaEntity>

    @Query(
        value = """
            SELECT p FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
              AND EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
                    AND s.judgeStatus IN :acceptedStatuses
              )
        """,
        countQuery = """
            SELECT COUNT(p) FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
              AND EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
                    AND s.judgeStatus IN :acceptedStatuses
              )
        """
    )
    fun findSolvedFiltered(
        @Param("keyword") keyword: String?,
        @Param("level") level: ProblemLevel?,
        @Param("algorithmType") algorithmType: AlgorithmType?,
        @Param("algorithmTagNames") algorithmTagNames: Collection<String>,
        @Param("memberId") memberId: Long,
        @Param("acceptedStatuses") acceptedStatuses: Collection<JudgeStatus>,
        pageable: Pageable
    ): Page<ProblemJpaEntity>

    @Query(
        value = """
            SELECT p FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
              AND EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
              )
              AND NOT EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
                    AND s.judgeStatus IN :acceptedStatuses
              )
        """,
        countQuery = """
            SELECT COUNT(p) FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
              AND EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
              )
              AND NOT EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
                    AND s.judgeStatus IN :acceptedStatuses
              )
        """
    )
    fun findAttemptedFiltered(
        @Param("keyword") keyword: String?,
        @Param("level") level: ProblemLevel?,
        @Param("algorithmType") algorithmType: AlgorithmType?,
        @Param("algorithmTagNames") algorithmTagNames: Collection<String>,
        @Param("memberId") memberId: Long,
        @Param("acceptedStatuses") acceptedStatuses: Collection<JudgeStatus>,
        pageable: Pageable
    ): Page<ProblemJpaEntity>

    @Query(
        value = """
            SELECT p FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
              AND NOT EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
              )
        """,
        countQuery = """
            SELECT COUNT(p) FROM ProblemJpaEntity p
            WHERE (:keyword IS NULL OR LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:level IS NULL OR p.level = :level)
              AND (:algorithmType IS NULL OR p.algorithmType = :algorithmType OR UPPER(p.primaryTag) IN :algorithmTagNames)
              AND NOT EXISTS (
                  SELECT s.id FROM SubmissionJpaEntity s
                  WHERE s.problem.id = p.id
                    AND s.member.id = :memberId
              )
        """
    )
    fun findUnsolvedFiltered(
        @Param("keyword") keyword: String?,
        @Param("level") level: ProblemLevel?,
        @Param("algorithmType") algorithmType: AlgorithmType?,
        @Param("algorithmTagNames") algorithmTagNames: Collection<String>,
        @Param("memberId") memberId: Long,
        pageable: Pageable
    ): Page<ProblemJpaEntity>
}
