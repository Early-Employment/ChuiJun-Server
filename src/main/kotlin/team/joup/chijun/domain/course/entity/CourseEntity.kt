package team.joup.chijun.domain.course.entity

import team.joup.chijun.domain.member.entity.MemberEntity
import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "courses")
class CourseEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    var teacher: MemberEntity,

    @Column(name = "course_name", nullable = false, length = 100)
    var courseName: String,

    var grade: Byte? = null,

    @Column(name = "class_number")
    var classNumber: Byte? = null,

    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)
