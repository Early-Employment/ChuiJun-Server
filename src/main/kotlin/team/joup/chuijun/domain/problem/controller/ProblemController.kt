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
import org.springframework.data.domain.Sort
import org.springframework.data.web.PageableDefault
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @Operation(summary = "문제 리스트 조회 및 검색 (페이징)", description = "시스템에 등록된 모든 문제 혹은 검색어가 포함된 문제 리스트를 페이징 처리하여 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공")
    ])
    @GetMapping
    fun getProblems(
        @Parameter(description = "검색할 문제 제목 키워드 (선택)", example = "두 수의 합")
        @RequestParam(name = "keyword", required = false) keyword: String?,
        @PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<GetProblemListResponse>> {
        val response = problemService.getProblemList(keyword, pageable)
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
