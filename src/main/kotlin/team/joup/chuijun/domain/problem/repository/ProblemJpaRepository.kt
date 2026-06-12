package team.joup.chuijun.domain.problem.repository

import org.springframework.data.jpa.repository.JpaRepository
import team.joup.chuijun.domain.problem.entity.ProblemJpaEntity

interface ProblemJpaRepository : JpaRepository<ProblemJpaEntity, Long>
