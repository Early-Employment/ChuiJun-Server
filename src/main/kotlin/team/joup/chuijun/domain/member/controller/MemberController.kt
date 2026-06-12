package team.joup.chuijun.domain.member.controller

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import team.joup.chuijun.domain.member.dto.response.GetMemberProfileResponse
import team.joup.chuijun.domain.member.service.MemberService

@RestController
@RequestMapping("/api/v1/members")
class MemberController(
    private val memberService: MemberService
) {

    @GetMapping("/{memberId}/profile")
    fun getMemberProfile(
        @PathVariable memberId: Long
    ): ResponseEntity<GetMemberProfileResponse> {
        val response = memberService.getMemberProfile(memberId)
        return ResponseEntity.ok(response)
    }
}
