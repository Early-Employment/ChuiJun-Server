package team.joup.chuijun.domain.classroom.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.classroom.dto.request.ClassroomCreateRequest
import team.joup.chuijun.domain.classroom.dto.request.ClassroomUpdateRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomResponse
import team.joup.chuijun.domain.classroom.dto.response.ClassroomTeacherDashboardResponse
import team.joup.chuijun.domain.classroom.service.ClassroomService

@Tag(name = "학급 (Classroom)", description = "선생님의 학급 생성 및 정보 관리 API")
@RestController
class ClassroomController(
    private val classroomService: ClassroomService
) {

    @GetMapping("/classrooms/{classroomId}/teacher-dashboard")
    fun getTeacherDashboard(
        @AuthenticationPrincipal loginUser: CustomUserDetails,
        @PathVariable classroomId: Long
    ): ResponseEntity<ClassroomTeacherDashboardResponse> {
        val response = classroomService.getTeacherDashboard(loginUser.id, classroomId)
        return ResponseEntity.ok(response)
    }
}

interface CustomUserDetails {
    val id: Long
}
