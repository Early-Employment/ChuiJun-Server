package team.joup.chuijun.domain.problem.entity

enum class ProblemLevel(val levelValue: Int) {
    LEVEL_1(1),
    LEVEL_2(2),
    LEVEL_3(3),
    LEVEL_4(4),
    LEVEL_5(5);

    val score: Int
        get() = levelValue * 5

    companion object {
        fun from(value: Int): ProblemLevel {
            return entries.find { it.levelValue == value }
                ?: throw IllegalArgumentException("유효하지 않은 난이도 레벨입니다: $value (1~5 사이만 가능)")
        }

        fun fromQuery(value: String?): ProblemLevel? {
            val normalized = value?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.uppercase()
                ?.replace(" ", "_")
                ?.replace(".", "")
                ?: return null

            return when (normalized) {
                "1", "LV1", "LV_1", "LEVEL1", "LEVEL_1", "LEVL1", "LEVL_1" -> LEVEL_1
                "2", "LV2", "LV_2", "LEVEL2", "LEVEL_2", "LEVL2", "LEVL_2" -> LEVEL_2
                "3", "LV3", "LV_3", "LEVEL3", "LEVEL_3", "LEVL3", "LEVL_3" -> LEVEL_3
                "4", "LV4", "LV_4", "LEVEL4", "LEVEL_4", "LEVL4", "LEVL_4" -> LEVEL_4
                "5", "LV5", "LV_5", "LEVEL5", "LEVEL_5", "LEVL5", "LEVL_5" -> LEVEL_5
                else -> throw IllegalArgumentException("유효하지 않은 난이도 레벨입니다: $value")
            }
        }
    }
}
