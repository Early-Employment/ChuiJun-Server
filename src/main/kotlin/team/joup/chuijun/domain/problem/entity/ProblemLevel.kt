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
    }
}
