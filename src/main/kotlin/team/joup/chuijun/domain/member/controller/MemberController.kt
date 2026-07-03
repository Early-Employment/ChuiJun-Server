package team.joup.chuijun.domain.member.controller

import io.swagger.v3.oas.annotations.Operation
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
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.member.dto.request.UpdateProfileImageRequest
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

    @Operation(summary = "전체 랭킹 리스트 조회")
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

    @Operation(summary = "내 대시보드 프로필 조회")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공"),
        ApiResponse(
            responseCode = "401",
            description = "인증되지 않은 사용자",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "404",
            description = "해당 회원을 찾을 수 없음",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        ),
        ApiResponse(
            responseCode = "500",
            description = "서버 내부 데이터 에러",
            content = [Content(schema = Schema(implementation = ErrorResponse::class))]
        )
    ])
    @GetMapping("/me")
    fun getMyProfile(
        @AuthenticationPrincipal userDetails: UserDetails?
    ): ResponseEntity<GetMemberProfileResponse> {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }

        val memberId = userDetails.username.toLongOrNull()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        val response = memberService.getMemberProfile(memberId)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "내 프로필 이미지 수정")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "수정 성공"),
        ApiResponse(responseCode = "401", description = "인증되지 않은 사용자"),
        ApiResponse(responseCode = "404", description = "해당 회원을 찾을 수 없음")
    ])
    @PutMapping("/me/profile-image")
    fun updateProfileImage(
        @AuthenticationPrincipal userDetails: UserDetails?,
        @RequestBody request: UpdateProfileImageRequest
    ): ResponseEntity<Void> {
        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        }
        val memberId = userDetails.username.toLongOrNull()
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()

        memberService.updateProfileImage(memberId, request.profileImageUrl)
        return ResponseEntity.ok().build()
    }
}
