package team.joup.chuijun.domain.problem.entity

enum class AlgorithmType(
    val label: String,
    private vararg val aliases: String
) {
    BRUTE_FORCE("브루트포스", "BRUTEFORCE", "완전탐색"),
    DP("DP", "DYNAMIC_PROGRAMMING", "동적계획법"),
    GREEDY("그리디", "GREEDY_ALGORITHM"),
    BFS("BFS", "너비우선탐색"),
    DFS("DFS", "깊이우선탐색"),
    BINARY_SEARCH("이분탐색", "BINARYSEARCH", "이진탐색");

    fun tagNames(): Set<String> {
        return (listOf(name, label) + aliases)
            .flatMap { raw -> listOf(raw.uppercase(), normalize(raw)) }
            .toSet()
    }

    companion object {
        private val lookupMap: Map<String, AlgorithmType> = entries
            .flatMap { entry -> entry.tagNames().map { tag -> tag to entry } }
            .toMap()

        fun fromQuery(value: String?): AlgorithmType? {
            if (value.isNullOrBlank()) return null
            return findByQuery(value)
                ?: throw IllegalArgumentException("유효하지 않은 알고리즘 유형입니다: $value")
        }

        fun findByQuery(value: String?): AlgorithmType? {
            val normalized = value?.trim()?.takeIf { it.isNotBlank() }?.let { normalize(it) } ?: return null
            return lookupMap[normalized]
        }

        fun normalize(value: String): String {
            return value.trim()
                .uppercase()
                .replace(" ", "")
                .replace("_", "")
                .replace("-", "")
        }
    }
}
