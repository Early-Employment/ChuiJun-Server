package team.joup.chuijun.domain.store.controller

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import team.joup.chuijun.domain.store.dto.request.PurchaseItemRequest
import team.joup.chuijun.domain.store.dto.response.GetStoreItemResponse
import team.joup.chuijun.domain.store.entity.ItemType
import team.joup.chuijun.domain.store.service.StoreService
import team.joup.chuijun.global.error.ErrorResponse

@Tag(name = "Store", description = "상점 및 아이템 API")
@RestController
@RequestMapping("/store")
class StoreController(
    private val storeService: StoreService
) {

    @Operation(summary = "상점 아이템 목록 조회 (카테고리 필터링)", description = "상점에 등록된 아이템 리스트를 조회합니다. 카테고리 파라미터가 없으면 전체를 조회하며, 넘겨주면 해당 종류만 필터링합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "조회 성공")
    ])
    @GetMapping
    fun getStoreItems(
        @RequestHeader("X-Member-Id") memberId: Long,
        @Parameter(description = "아이템 카테고리 종류 (선택: BADGE, THEME, FRAME, EFFECT)", example = "BADGE")
        @RequestParam(name = "category", required = false) type: ItemType?
    ): ResponseEntity<List<GetStoreItemResponse>> {
        val response = storeService.getStoreItems(memberId, type)
        return ResponseEntity.ok(response)
    }

    @Operation(summary = "아이템 구매", description = "보유한 코인을 사용하여 상점의 특정 아이템을 구매하고 내 보관함에 추가합니다.")
    @ApiResponses(value = [
        ApiResponse(responseCode = "200", description = "구매 성공 및 보관함 추가 완료"),
        ApiResponse(responseCode = "400", description = "구매 실패 (코인 부족 또는 이미 보유한 아이템)", content = [Content(schema = Schema(implementation = ErrorResponse::class))]),
        ApiResponse(responseCode = "404", description = "회원 또는 아이템을 찾을 수 없음", content = [Content(schema = Schema(implementation = ErrorResponse::class))])
    ])
    @PostMapping("/purchase")
    fun purchaseItem(
        @RequestHeader("X-Member-Id") memberId: Long,
        @RequestBody request: PurchaseItemRequest
    ): ResponseEntity<Void> {
        storeService.purchaseItem(memberId, request.itemId)
        return ResponseEntity.ok().build()
    }
}
