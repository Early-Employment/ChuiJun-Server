package team.joup.chuijun.domain.member.service

import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import java.time.LocalDateTime
import java.util.NoSuchElementException

@Service
class ProfileUpdateService(
    private val memberJpaRepository: MemberJpaRepository
) {

    // 💡 실제 DB 정보만 수정하는 영역에만 타이트하게 트랜잭션을 적용하여 커넥션 풀 고갈을 방지합니다.
    @Transactional
    fun updateProfileImageUrl(memberId: Long, profileImageUrl: String) {
        val member = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        member.profileImageUrl = profileImageUrl
        member.updatedAt = LocalDateTime.now()
    }
}
