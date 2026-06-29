package team.joup.chuijun.domain.submission.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.joup.chuijun.domain.submission.dto.request.SubmitProblemRequest
import team.joup.chuijun.domain.submission.dto.response.SubmitProblemResponse
import team.joup.chuijun.domain.submission.service.SubmissionService

@Tag(name = "Submission", description = "문제 제출 및 채점 기록을 관리하는 API입니다.")
@RestController
@RequestMapping("/submissions")
class SubmissionController(
    private val submissionService: SubmissionService
) {

    @Operation(summary = "문제 소스코드 제출 및 채점 결과 반영", description = "프론트엔드 컴파일러가 판정한 결과와 소스코드를 전달받아 제출 기록을 생성하고 유저 점수를 갱신합니다.")
    @PostMapping
    fun submitProblem(
        @RequestBody @Valid request: SubmitProblemRequest
    ): ResponseEntity<SubmitProblemResponse> {
        val response = submissionService.submitProblem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }
}
