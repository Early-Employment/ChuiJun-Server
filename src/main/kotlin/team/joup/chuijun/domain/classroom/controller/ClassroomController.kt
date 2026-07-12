package team.joup.chuijun.domain.classroom.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
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

    @Operation(summary = "학급 개설", description = "새로운 수업 학급을 개설합니다. 학생 역할의 회원은 개설이 불가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "학급 개설 성공"),
        ApiResponse(responseCode = "400", description = "학생 권한으로 개설을 시도함"),
        ApiResponse(responseCode = "404", description = "회원 정보를 찾을 수 없음")
    ])
    @PostMapping("/classrooms")
    fun createClassroom(
        @Parameter(description = "개설할 선생님 회원 ID", example = "1") @RequestParam teacherId: Long,
        @RequestBody request: ClassroomCreateRequest
    ): ResponseEntity<Long> {
        val classroomId = classroomService.createClassroom(teacherId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomId)
    }

    @Operation(summary = "학급 상세 조회", description = "특정 학급의 기본 정보를 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "학급 조회 성공"),
        ApiResponse(responseCode = "404", description = "학급을 찾을 수 없음")
    ])
    @GetMapping("/classrooms/{classroomId}")
    fun getClassroom(
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long
    ): ResponseEntity<ClassroomResponse> {
        val response = classroomService.getClassroom(classroomId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "선생님별 개설 학급 목록 조회", description = "특정 선생님이 개설한 모든 학급 목록을 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "학급 목록 조회 성공")
    ])
    @GetMapping("/classrooms")
    fun getClassroomsByTeacher(
        @Parameter(description = "조회할 선생님 회원 ID", example = "1") @RequestParam teacherId: Long
    ): ResponseEntity<List<ClassroomResponse>> {
        val response = classroomService.getClassroomsByTeacher(teacherId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "학급 정보 수정", description = "학급 정보(이름, 학년, 반)를 수정합니다. 해당 학급을 개설한 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "학급 정보 수정 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "학급 또는 회원 정보를 찾을 수 없음")
    ])
    @PutMapping("/classrooms/{classroomId}")
    fun updateClassroom(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long,
        @RequestBody request: ClassroomUpdateRequest
    ): ResponseEntity<Unit> {
        classroomService.updateClassroom(requestorId, classroomId, request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "학급 폐쇄(삭제)", description = "학급을 삭제합니다. 해당 학급을 개설한 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "학급 삭제 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "학급 또는 회원 정보를 찾을 수 없음")
    ])
    @DeleteMapping("/classrooms/{classroomId}")
    fun deleteClassroom(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long
    ): ResponseEntity<Unit> {
        classroomService.deleteClassroom(requestorId, classroomId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "선생님 학급 대시보드 조회", description = "선생님 페이지에 필요한 네 가지 핵심 통계 지표와 학생 및 과제 제출 현황을 한 번에 가져옵니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "대시보드 조회 성공"),
        ApiResponse(responseCode = "400", description = "조회 권한 없음"),
        ApiResponse(responseCode = "404", description = "학급 혹은 회원 없음")
    ])
    @GetMapping("/classrooms/{classroomId}/teacher-dashboard")
    fun getTeacherDashboard(
        @Parameter(description = "요청 선생님 회원 ID", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long
    ): ResponseEntity<ClassroomTeacherDashboardResponse> {
        val response = classroomService.getTeacherDashboard(requestorId, classroomId)
        return ResponseEntity.ok(response)
    }
}
