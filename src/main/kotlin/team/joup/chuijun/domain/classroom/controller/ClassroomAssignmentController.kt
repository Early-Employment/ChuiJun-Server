package team.joup.chuijun.domain.classroom.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.classroom.dto.request.ClassroomAssignmentCreateRequest
import team.joup.chuijun.domain.classroom.dto.request.ClassroomAssignmentUpdateRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomAssignmentResponse
import team.joup.chuijun.domain.classroom.service.ClassroomAssignmentService

@Tag(name = "과제 (Assignment)", description = "선생님의 과제 출제 및 관리 API")
@RestController
class ClassroomAssignmentController(
    private val assignmentService: ClassroomAssignmentService
) {

    @Operation(summary = "과제 출제", description = "특정 학급에 새로운 문제 과제를 배정합니다. 해당 학급의 담당 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "과제 출제 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "학급 또는 문제를 찾을 수 없음")
    ])
    @PostMapping("/classrooms/{classroomId}/assignments")
    fun assignProblem(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long,
        @RequestBody request: ClassroomAssignmentCreateRequest
    ): ResponseEntity<Long> {
        val assignmentId = assignmentService.assignProblem(requestorId, classroomId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(assignmentId)
    }

    @Operation(summary = "학급별 과제 목록 조회", description = "특정 학급에 출제된 모든 과제 목록을 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "과제 목록 조회 성공")
    ])
    @GetMapping("/classrooms/{classroomId}/assignments")
    fun getAssignmentsByClassroom(
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long
    ): ResponseEntity<List<ClassroomAssignmentResponse>> {
        val response = assignmentService.getAssignmentsByClassroom(classroomId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "과제 정보 수정", description = "출제된 과제(마감일, 필수 여부)를 수정합니다. 해당 과제가 속한 학급의 담당 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "과제 수정 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "과제를 찾을 수 없음")
    ])
    @PutMapping("/assignments/{assignmentId}")
    fun updateAssignment(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "과제 식별자 ID", example = "1") @PathVariable assignmentId: Long,
        @RequestBody request: ClassroomAssignmentUpdateRequest
    ): ResponseEntity<Unit> {
        assignmentService.updateAssignment(requestorId, assignmentId, request)
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "과제 삭제(배정 취소)", description = "출제된 과제를 삭제합니다. 해당 과제가 속한 학급의 담당 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "과제 삭제 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "과제를 찾을 수 없음")
    ])
    @DeleteMapping("/assignments/{assignmentId}")
    fun deleteAssignment(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "과제 식별자 ID", example = "1") @PathVariable assignmentId: Long
    ): ResponseEntity<Unit> {
        assignmentService.deleteAssignment(requestorId, assignmentId)
        return ResponseEntity.noContent().build()
    }
}
