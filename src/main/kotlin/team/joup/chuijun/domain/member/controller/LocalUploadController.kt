package team.joup.chuijun.domain.member.controller

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
class LocalUploadController(
    @Value("\${local.upload.dir:C:/uploads/profiles/}")
    private val uploadDir: String
) {

    @PutMapping("/api/local-upload/{fileName}")
    fun uploadFile(
        @PathVariable fileName: String,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<Map<String, String>> {
        val baseDir = File(uploadDir).canonicalFile
        val dest = File(baseDir, fileName).canonicalFile

        if (!dest.toPath().startsWith(baseDir.toPath())) {
            throw IllegalArgumentException("잘못된 파일 경로입니다.")
        }

        file.transferTo(dest)
        return ResponseEntity.ok(mapOf("status" to "success"))
    }
}
