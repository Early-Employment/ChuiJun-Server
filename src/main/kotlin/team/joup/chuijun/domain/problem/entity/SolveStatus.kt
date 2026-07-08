package team.joup.chuijun.domain.problem.entity

enum class SolveStatus {
    SOLVED,
    UNSOLVED,
    ATTEMPTED;

    companion object {
        fun fromQuery(value: String?): SolveStatus? {
            val normalized = value?.trim()
                ?.takeIf { it.isNotBlank() }
                ?.uppercase()
                ?.replace(" ", "")
                ?.replace("_", "")
                ?.replace("-", "")
                ?: return null

            return when (normalized) {
                "SOLVED", "푼문제", "해결함", "풀었음", "해결" -> SOLVED
                "UNSOLVED", "안푼문제", "안품", "안풀림", "미해결" -> UNSOLVED
                "ATTEMPTED", "시도한문제", "시도", "시도함" -> ATTEMPTED
                else -> throw IllegalArgumentException("유효하지 않은 풀이 상태입니다: $value")
            }
        }
    }
}
