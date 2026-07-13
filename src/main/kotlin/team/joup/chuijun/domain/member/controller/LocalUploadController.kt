package team.joup.chuijun.domain.member.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.data.repository.findByIdOrNull
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import team.joup.chuijun.domain.member.repository.MemberJpaRepository
import java.io.File
import java.time.LocalDateTime
import java.util.UUID

@RestController
class LocalUploadController(
    @Value("\${local.upload.dir:C:/uploads/profiles/}")
    private val uploadDir: String,

    @Value("\${local.upload.server-url:https://chuijun.https://gsmsv.site/}")
    private val serverUrl: String,

    private val memberJpaRepository: MemberJpaRepository
) {

    @PutMapping("/api/local-upload")
    @Transactional
    fun uploadFile(
        @AuthenticationPrincipal loginUser: User,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            throw IllegalArgumentException("업로드할 파일이 비어 있습니다.")
        }

        // 1. 저장할 디렉토리 준비
        val baseDir = File(uploadDir).canonicalFile
        if (!baseDir.exists()) {
            baseDir.mkdirs()
        }

        // 2. 랜덤 파일명 생성 (파일명 중복 및 덮어쓰기 방지)
        val originalFilename = file.originalFilename ?: "image.png"
        val extension = originalFilename.substringAfterLast(".", "png")
        val savedFileName = "${UUID.randomUUID()}.$extension"

        // 3. 파일 저장 경로 설정 및 상위 경로 탈출 보안 검증 (Directory Traversal 방지)
        val dest = File(baseDir, savedFileName).canonicalFile
        if (!dest.toPath().startsWith(baseDir.toPath())) {
            throw IllegalArgumentException("잘못된 파일 경로입니다.")
        }

        // 4. 서버 로컬 스토리지에 파일 물리 저장
        file.transferTo(dest)

        // 5. 프론트엔드가 사용할 수 있는 외부 웹 자원 주소(URL) 조립
        val baseUrl = serverUrl.removeSuffix("/")
        val profileImageUrl = "$baseUrl/uploads/profiles/$savedFileName"

        // 6. [서비스 클래스 없이 직접 처리] 로그인한 유저 엔티티 조회 및 DB 반영
        val memberId = loginUser.username.toLong()
        val member = memberJpaRepository.findByIdOrNull(memberId)
            ?: throw NoSuchElementException("존재하지 않는 회원입니다. ID: $memberId")

        // @Transactional 환경이므로 객체의 값만 바꾸면 알아서 DB에 UPDATE 쿼리가 날아갑니다.
        member.profileImageUrl = profileImageUrl
        member.updatedAt = LocalDateTime.now()

        // 7. 성공 결과 및 저장된 이미지 URL 반환
        return ResponseEntity.ok(
            mapOf(
                "status" to "success",
                "imageUrl" to profileImageUrl
            )
        )
    }
}
