package team.joup.chuijun.domain.problem.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.joup.chuijun.domain.problem.dto.response.GetProblemDetailResponse
import team.joup.chuijun.domain.problem.dto.response.GetProblemListResponse
import team.joup.chuijun.domain.problem.service.ProblemService
import team.joup.chuijun.global.error.ErrorResponse

@Tag(name = "Problem", description = "문제 관련 API")
@RestController
@RequestMapping("/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    @Operation(summary = "문제 전체 리스트 조회 (페이징)", description = "시스템에 등록된 모든 알고리즘 문제 리스트를 페이징 처리하여 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공")
    ])
    @GetMapping
    fun getProblems(
        @PageableDefault(size = 20) pageable: Pageable
    ): ResponseEntity<Page<GetProblemListResponse>> {
        val response = problemService.getProblemList(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "문제 상세 조회 (IDE 단면)", description = "특정 문제의 마크다운 지문 및 공개용 예제 테스트 케이스만 상세 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(
            responseCode = "404",
            description = "해당 문제를 찾을 수 없음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @GetMapping("/{problemId}")
    fun getProblemDetail(
        @Parameter(description = "조회할 문제의 고유 ID", example = "1")
        @PathVariable("problemId") problemId: Long
    ): ResponseEntity<GetProblemDetailResponse> {
        val response = problemService.getProblemDetail(problemId)
        return ResponseEntity.ok(response)
    }
}
