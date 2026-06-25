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
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.problem.dto.request.CreateProblemRequest
import team.joup.chuijun.domain.problem.dto.request.UpdateProblemRequest
import team.joup.chuijun.domain.problem.dto.response.GetProblemDetailResponse
import team.joup.chuijun.domain.problem.dto.response.GetProblemListResponse
import team.joup.chuijun.domain.problem.service.ProblemService
import team.joup.chuijun.global.error.ErrorResponse

@Tag(name = "Problem", description = "문제(Problem) 도메인의 등록, 조회, 수정, 삭제를 관리하는 API입니다.")
@RestController
@RequestMapping("/problems")
class ProblemController(
    private val problemService: ProblemService
) {

    @Operation(summary = "문제 리스트 조회 및 검색 (페이징)", description = "시스템에 등록된 전체 문제 목록을 페이징 처리하여 조회합니다. 특정 키워드로 문제 제목을 검색할 수 있습니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "문제 목록 리스트 페이징 조회 성공")
    ])
    @GetMapping
    fun getProblems(
        @Parameter(description = "검색할 문제 제목의 키워드 (선택 사항)", example = "준건이")
        @RequestParam(name = "keyword", required = false) keyword: String?,

        @Parameter(description = "페이징 및 정렬 정보", example = "{\"page\": 0, \"size\": 20, \"sort\": [\"id,desc\"]}")
        @PageableDefault(size = 20, sort = ["id"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<GetProblemListResponse>> {
        val response = problemService.getProblemList(keyword, pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "문제 상세 조회 (IDE 단면)", description = "특정 문제의 마크다운 지문 정보 및 화면 UI(IDE)에 렌더링할 공개용 예제 테스트 케이스 목록을 상세 조회합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "문제 상세 데이터 및 공개 테스트 케이스 조회 성공"),
        ApiResponse(
            responseCode = "404",
            description = "요청한 고유 ID에 해당하는 문제가 시스템에 존재하지 않음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @GetMapping("/{problemId}")
    fun getProblemDetail(
        @Parameter(description = "조회하고자 하는 문제의 고유 식별자(ID)", example = "1", required = true)
        @PathVariable("problemId") problemId: Long
    ): ResponseEntity<GetProblemDetailResponse> {
        val response = problemService.getProblemDetail(problemId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "신규 문제 등록", description = "출제자가 작성한 새로운 문제 지문 정보와 연관된 테스트 케이스(공개/비공개) 목록을 일괄 등록합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "201", description = "문제가 성공적으로 생성됨. 바디로 생성된 문제의 ID를 반환합니다."),
        ApiResponse(
            responseCode = "400",
            description = "입력 데이터 값의 유효성 검증 실패 또는 비정상적인 데이터 구조",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @PostMapping
    fun createProblem(
        @RequestBody request: CreateProblemRequest
    ): ResponseEntity<Long> {
        val problemId = problemService.createProblem(request)
        return ResponseEntity.status(HttpStatus.CREATED).body(problemId)
    }

    @Operation(summary = "기존 문제 및 테스트 케이스 수정", description = "특정 문제의 지문 내용, 제한 조건(메모리/시간), 상태값과 함께 동반된 테스트 케이스 목록을 원자적으로 일괄 갱신합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "문제 정보 및 테스트 케이스가 성공적으로 갱신됨 (반환 데이터 없음)"),
        ApiResponse(
            responseCode = "404",
            description = "수정하려는 고유 ID의 문제가 시스템에 존재하지 않음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @PutMapping("/{problemId}")
    fun updateProblem(
        @Parameter(description = "수정하고자 하는 기존 문제의 고유 식별자(ID)", example = "1", required = true)
        @PathVariable("problemId") problemId: Long,
        @RequestBody request: UpdateProblemRequest
    ): ResponseEntity<Void> {
        problemService.updateProblem(problemId, request)
        return ResponseEntity.noContent().build()
    }

    @Operation(summary = "문제 영구 삭제", description = "시스템 내에서 특정 문제를 영구적으로 파기합니다. 이 자식 테이블인 관련 테스트 케이스 데이터도 데이터 무결성을 위해 함께 물리 삭제됩니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "204", description = "문제 및 하위 테스트 케이스들이 안전하게 물리 삭제됨"),
        ApiResponse(
            responseCode = "404",
            description = "삭제하려는 고유 ID의 문제가 데이터베이스에 존재하지 않음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @DeleteMapping("/{problemId}")
    fun deleteProblem(
        @Parameter(description = "삭제하고자 하는 문제의 고유 식별자(ID)", example = "1", required = true)
        @PathVariable("problemId") problemId: Long
    ): ResponseEntity<Void> {
        problemService.deleteProblem(problemId)
        return ResponseEntity.noContent().build()
    }
}
