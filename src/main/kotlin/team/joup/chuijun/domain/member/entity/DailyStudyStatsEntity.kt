package team.joup.chuijun.domain.member.entity

import jakarta.persistence.*
import java.io.Serializable
import java.time.LocalDate

@Embeddable
data class DailyStudyStatsId(
    @Column(name = "member_id")
    val memberId: Long,
    @Column(name = "study_date")
    val studyDate: LocalDate
) : Serializable

@Entity
@Table(name = "daily_study_stats")
class DailyStudyStatsEntity(
    @EmbeddedId
    val id: DailyStudyStatsId,

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("memberId")
    @JoinColumn(name = "member_id", nullable = false)
    val member: MemberEntity,

    @Column(name = "study_seconds", nullable = false)
    var studySeconds: Int = 0,

    @Column(name = "solved_count", nullable = false)
    var solvedCount: Int = 0,

    @Column(name = "submit_count", nullable = false)
    var submitCount: Int = 0
)
