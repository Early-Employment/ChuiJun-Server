package team.joup.chuijun.domain.member.controller

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
import team.joup.chuijun.domain.member.dto.response.GetMemberRankingResponse
import team.joup.chuijun.domain.member.dto.response.GetMemberProfileResponse
import team.joup.chuijun.domain.member.service.MemberService
import team.joup.chuijun.global.error.ErrorResponse

@Tag(name = "Member", description = "회원 관련 API")
@RestController
@RequestMapping("/members")
class MemberController(
    private val memberService: MemberService
) {

    @Operation(summary = "전체 랭킹 리스트 조회", description = "회원들의 레이팅 점수를 기준으로 정렬된 랭킹 리스트를 페이징 조회합니다. 기본 정렬은 레이팅(rating) 내림차순입니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공")
    ])
    @GetMapping("/rankings")
    fun getRankings(
        @PageableDefault(size = 20, sort = ["rating"], direction = Sort.Direction.DESC) pageable: Pageable
    ): ResponseEntity<Page<GetMemberRankingResponse>> {
        val response = memberService.getRankings(pageable)
        return ResponseEntity.ok(response)
    }

    @Operation(
        summary = "회원 대시보드 프로필 조회",
        description = "지정한 회원의 프로필 정보, 잔디 통계(최근 1년), 총 푼 문제 수, 최근 제출 활동(4건)을 조회합니다."
    )
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(
            responseCode = "404",
            description = "해당 회원을 찾을 수 없음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 데이터 에러 (ID가 null인 경우 등)",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @GetMapping("/{memberId}/profile")
    fun getMemberProfile(
        @Parameter(description = "조회할 회원의 고유 ID", example = "1")
        @PathVariable("memberId") memberId: Long
    ): ResponseEntity<GetMemberProfileResponse> {
        val response = memberService.getMemberProfile(memberId)
        return ResponseEntity.ok(response)
    }
}
