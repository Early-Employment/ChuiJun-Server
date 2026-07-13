package team.joup.chuijun.domain.classroom.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.User
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.classroom.dto.request.ClassroomCreateRequest
import team.joup.chuijun.domain.classroom.dto.request.ClassroomUpdateRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomResponse
import team.joup.chuijun.domain.classroom.dto.response.ClassroomTeacherDashboardResponse
import team.joup.chuijun.domain.classroom.service.ClassroomService

@Tag(name = "학급 (Classroom)", description = "선생님의 학급 생성, 정보 관리 및 통계 대시보드 API")
@RestController
class ClassroomController(
    private val classroomService: ClassroomService
) {

    @Operation(summary = "학급 생성", description = "선생님이 새로운 학급을 개설합니다. (학생 권한은 생성 불가)")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "학급 생성 성공 (생성된 ID 반환)"),
        ApiResponse(responseCode = "400", description = "학생 권한으로 생성 시도 시 에러")
    ])
    @PostMapping("/classrooms")
    fun createClassroom(
        @AuthenticationPrincipal loginUser: User,
        @RequestBody request: ClassroomCreateRequest
    ): ResponseEntity<Long> {
        val memberId = loginUser.username.toLong()
        val classroomId = classroomService.createClassroom(memberId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomId)
    }

    @Operation(summary = "단일 학급 정보 조회", description = "학급 ID를 통해 해당 학급의 기본 정보를 조회합니다.")
    @GetMapping("/classrooms/{classroomId}")
    fun getClassroom(
        @Parameter(description = "조회할 학급 ID") @PathVariable classroomId: Long
    ): ResponseEntity<ClassroomResponse> {
        val response = classroomService.getClassroom(classroomId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "담당 학급 목록 조회", description = "현재 로그인한 선생님이 담당하고 있는 모든 학급 목록을 조회합니다.")
    @GetMapping("/classrooms/teacher")
    fun getClassroomsByTeacher(
        @AuthenticationPrincipal loginUser: User
    ): ResponseEntity<List<ClassroomResponse>> {
        val memberId = loginUser.username.toLong()
        val response = classroomService.getClassroomsByTeacher(memberId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "학급 정보 수정", description = "학급의 이름, 학년, 반 정보를 수정합니다. (담당 선생님 또는 관리자만 가능)")
    @PutMapping("/classrooms/{classroomId}")
    fun updateClassroom(
        @AuthenticationPrincipal loginUser: User,
        @Parameter(description = "수정할 학급 ID") @PathVariable classroomId: Long,
        @RequestBody request: ClassroomUpdateRequest
    ): ResponseEntity<Void> {
        val memberId = loginUser.username.toLong()
        classroomService.updateClassroom(memberId, classroomId, request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "학급 삭제", description = "학급을 폐쇄(삭제)합니다. (담당 선생님 또는 관리자만 가능)")
    @DeleteMapping("/classrooms/{classroomId}")
    fun deleteClassroom(
        @AuthenticationPrincipal loginUser: User,
        @Parameter(description = "삭제할 학급 ID") @PathVariable classroomId: Long
    ): ResponseEntity<Void> {
        val memberId = loginUser.username.toLong()
        classroomService.deleteClassroom(memberId, classroomId)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "선생님 대시보드 조회", description = "특정 학급의 통계 데이터(제출률, 정답률, 학생 목록 등)를 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "대시보드 데이터 조회 성공"),
        ApiResponse(responseCode = "403", description = "타 선생님의 학급 대시보드 조회 시도 시 에러")
    ])
    @GetMapping("/classrooms/{classroomId}/teacher-dashboard")
    fun getTeacherDashboard(
        @AuthenticationPrincipal loginUser: User,
        @Parameter(description = "대시보드를 확인할 학급 ID") @PathVariable classroomId: Long
    ): ResponseEntity<ClassroomTeacherDashboardResponse> {
        val memberId = loginUser.username.toLong()
        val response = classroomService.getTeacherDashboard(memberId, classroomId)
        return ResponseEntity.ok(response)
    }
}
