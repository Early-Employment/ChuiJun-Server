package team.joup.chuijun.domain.member.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import team.joup.chuijun.domain.member.service.ProfileUpdateService
import java.io.File
import java.util.UUID

@RestController
class LocalUploadController(
    @Value("\${local.upload.dir:C:/uploads/profiles/}")
    private val uploadDir: String,

    // 💡 피드백 적용: 중복되어 있던 https:// 제거 및 올바른 주소 형식으로 교정
    @Value("\${local.upload.server-url:https://chuijun.gsmsv.site/}")
    private val serverUrl: String,

    private val profileUpdateService: ProfileUpdateService
) {

    // 💡 피드백 적용: 컨트롤러 자체에 @Transactional을 제거하여 파일 I/O 도중 DB 커넥션을 잡지 않도록 수정
    @PutMapping("/api/local-upload")
    fun uploadFile(
        @AuthenticationPrincipal loginUser: User,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        if (file.isEmpty) {
            throw IllegalArgumentException("업로드할 파일이 비어 있습니다.")
        }

        // 1. 디렉토리 준비
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

        // 4. 서버 로컬 스토리지에 파일 물리 저장 (트랜잭션 바깥에서 처리)
        file.transferTo(dest)

        // 5. 💡 피드백 적용: WebConfig의 리소스 규칙과 일치하도록 /images/profiles/ 포맷으로 조립
        val baseUrl = serverUrl.removeSuffix("/")
        val profileImageUrl = "$baseUrl/images/profiles/$savedFileName"

        // 6. 💡 피드백 적용: DB 반영 처리는 트랜잭션이 분리된 서비스 레이어로 위임
        val memberId = loginUser.username.toLong()
        profileUpdateService.updateProfileImageUrl(memberId, profileImageUrl)

        // 7. 성공 결과 및 저장된 이미지 URL 반환
        return ResponseEntity.ok(
            mapOf(
                "status" to "success",
                "imageUrl" to profileImageUrl
            )
        )
    }
}
