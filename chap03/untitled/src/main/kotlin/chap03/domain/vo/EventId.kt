package chap03.domain.vo

class EventId(val id : String) {

    companion object {
        fun of(id: String): EventId {
            return EventId(id)
        }
    }
}