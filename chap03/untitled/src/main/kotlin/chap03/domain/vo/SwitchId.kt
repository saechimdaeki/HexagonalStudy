package chap03.domain.vo

import java.util.*


data class SwitchId (val id: UUID) {

    companion object {
        fun withId(id: String?): SwitchId {
            return SwitchId(UUID.fromString(id))
        }

        fun withoutId(): SwitchId {
            return SwitchId(UUID.randomUUID())
        }
    }
}
