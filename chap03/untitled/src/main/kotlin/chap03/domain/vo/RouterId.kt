package chap03.domain.vo

import java.util.*


data class RouterId (val id: UUID) {

    companion object {
        fun withId(id: String?): RouterId {
            return RouterId(UUID.fromString(id))
        }

        fun withoutId(): RouterId {
            return RouterId(UUID.randomUUID())
        }
    }
}
