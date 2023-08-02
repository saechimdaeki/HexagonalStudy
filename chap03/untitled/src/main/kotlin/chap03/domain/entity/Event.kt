package chap03.domain.entity

import chap03.domain.policy.RegexEventParser
import chap03.domain.policy.SplitEventParser
import chap03.domain.vo.Activity
import chap03.domain.vo.EventId
import chap03.domain.vo.ParsePolicyType
import chap03.domain.vo.Protocol
import java.time.OffsetDateTime
import java.util.*


class Event(private val timestamp: OffsetDateTime,
            private val id: EventId,
            private val protocol: Protocol,
            private val activity: Activity
) : Comparable<Event> {

    override fun compareTo(event: Event): Int {
        return timestamp.compareTo(event.timestamp)
    }

    override fun equals(obj: Any?): Boolean {
        return if (obj is Event) {
            (obj.timestamp == timestamp && obj.id.equals(id)
                    && obj.protocol.equals(protocol)
                    && obj.activity.equals(activity))
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(timestamp, id, protocol, activity) + 31
    }

    companion object {
        fun parsedEvent(unparsedEvent: String?, policy: ParsePolicyType?): Event {
            return when (policy) {
                ParsePolicyType.REGEX -> RegexEventParser().parseEvent(unparsedEvent)
                ParsePolicyType.SPLIT -> SplitEventParser().parseEvent(unparsedEvent)
                else -> throw IllegalArgumentException("")
            }
        }
    }
}
