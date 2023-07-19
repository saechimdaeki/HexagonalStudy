package chap01.domain


class RouterId private constructor(private val id: String) {
    override fun toString(): String {
        return "RouterId{" +
                "id='" + id + '\'' +
                '}'
    }

    companion object {
        fun of(id: String): RouterId {
            return RouterId(id)
        }
    }
}
