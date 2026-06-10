package team.joup.chuijun.domain.member.repository

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import team.joup.chuijun.domain.member.entity.DailyStudyStatsJpaEntity
import team.joup.chuijun.domain.member.entity.DailyStudyStatsId
import java.time.LocalDate

interface DailyStudyStatsJpaRepository : JpaRepository<DailyStudyStatsJpaEntity, DailyStudyStatsId> {

    @Query("SELECT d FROM DailyStudyStatsJpaEntity d WHERE d.id.memberId = :memberId AND d.id.studyDate >= :startDate")
    fun findCurrentYearStats(
        @Param("memberId") memberId: Long,
        @Param("startDate") startDate: LocalDate
    ): List<DailyStudyStatsJpaEntity>
}
