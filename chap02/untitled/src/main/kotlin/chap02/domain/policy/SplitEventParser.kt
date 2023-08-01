package chap02.domain.policy

import chap02.domain.entity.Event
import chap02.domain.vo.Activity
import chap02.domain.vo.EventId
import chap02.domain.vo.Protocol
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*


class SplitEventParser : EventParser {
    override fun parseEvent(event: String?): Event {
        val fields = Arrays.asList(*event!!.split(" ".toRegex()).dropLastWhile { it.isEmpty() }
            .toTypedArray())
        val timestamp = LocalDateTime.parse(fields[0], formatter).atOffset(ZoneOffset.UTC)
        val id = EventId.of(fields[1])
        val protocol = Protocol.valueOf(fields[2])
        val activity = Activity(fields[3], fields[5])
        return Event(timestamp, id, protocol, activity)
    }
}
