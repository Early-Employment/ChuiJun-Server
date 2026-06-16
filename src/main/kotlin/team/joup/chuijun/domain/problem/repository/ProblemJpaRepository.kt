package team.joup.chuijun.domain.problem.repository

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity

interface ProblemJpaRepository : JpaRepository<ProblemJpaEntity, Long> {
    fun findByTitleContaining(title: String, pageable: Pageable): Page<ProblemJpaEntity>
}
