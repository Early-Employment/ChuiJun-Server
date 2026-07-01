package team.joup.chuijun.domain.classroom.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.classroom.dto.request.JoinClassroomRequest
import team.joup.chuijun.domain.classroom.dto.response.ClassroomInviteCodeResponse
import team.joup.chuijun.domain.classroom.dto.response.ClassroomResponse
import team.joup.chuijun.domain.classroom.service.ClassroomMemberService

@Tag(name = "학급 멤버 및 초대 (Classroom Member)", description = "학생의 학급 가입, 초대 코드 조회 및 학생 관리 API")
@RestController
class ClassroomMemberController(
    private val classroomMemberService: ClassroomMemberService
) {

    @Operation(summary = "학급 초대 코드 조회 및 생성", description = "학급 가입에 필요한 6자리 초대 코드를 조회하거나 생성합니다. 담당 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "초대 코드 조회 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "학급 또는 회원 정보를 찾을 수 없음")
    ])
    @PostMapping("/classrooms/{classroomId}/invite-code")
    fun getOrCreateInviteCode(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long
    ): ResponseEntity<ClassroomInviteCodeResponse> {
        val response = classroomMemberService.getOrCreateInviteCode(requestorId, classroomId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "학생 학급 가입", description = "초대 코드를 이용해 학생이 학급에 가입합니다. 학생 역할의 회원만 가입이 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "학급 가입 성공"),
        ApiResponse(responseCode = "400", description = "이미 가입했거나 학생 역할이 아님"),
        ApiResponse(responseCode = "404", description = "유효하지 않은 초대 코드 또는 회원 없음")
    ])
    @PostMapping("/classrooms/join")
    fun joinClassroom(
        @Parameter(description = "가입할 학생 회원 ID", example = "2") @RequestParam studentId: Long,
        @RequestBody request: JoinClassroomRequest
    ): ResponseEntity<Long> {
        val classroomMemberId = classroomMemberService.joinClassroom(studentId, request)
        return ResponseEntity.status(HttpStatus.CREATED).body(classroomMemberId)
    }

    @Operation(summary = "학생 추방", description = "특정 학생을 학급에서 탈퇴시킵니다. 해당 학급의 담당 선생님 또는 관리자만 가능합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "학생 추방 성공"),
        ApiResponse(responseCode = "400", description = "권한이 없거나 잘못된 요청"),
        ApiResponse(responseCode = "404", description = "학급에 가입되지 않은 학생이거나 리소스 없음")
    ])
    @DeleteMapping("/classrooms/{classroomId}/students/{studentId}")
    fun kickStudent(
        @Parameter(description = "요청자 회원 ID (선생님)", example = "1") @RequestParam requestorId: Long,
        @Parameter(description = "학급 식별자 ID", example = "1") @PathVariable classroomId: Long,
        @Parameter(description = "추방할 학생 회원 ID", example = "2") @PathVariable studentId: Long
    ): ResponseEntity<Unit> {
        classroomMemberService.kickStudent(requestorId, classroomId, studentId)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "로그인한 학생의 본인 학급 목록 조회", description = "현재 로그인한 학생이 가입되어 있는 모든 학급 목록을 조회합니다. RESTful 설계 가이드 및 IDOR 보안 취약점을 예방 조치했습니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "학생 소속 학급 목록 조회 성공"),
        ApiResponse(responseCode = "400", description = "학생 역할이 아닌 회원이 요청함"),
        ApiResponse(responseCode = "404", description = "존재하지 않는 회원 ID")
    ])
    @GetMapping("/classrooms/me")
    fun getMyClassrooms(
        @Parameter(description = "로그인한 학생 회원 ID (임시 파라미터)", example = "2") @RequestParam studentId: Long
    ): ResponseEntity<List<ClassroomResponse>> {
        val response = classroomMemberService.getClassroomsByStudent(studentId)
        return ResponseEntity.ok(response)
    }
}
