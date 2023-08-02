package chap03.domain.policy

import chap03.domain.entity.Event
import chap03.domain.vo.Activity
import chap03.domain.vo.EventId
import chap03.domain.vo.Protocol
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.regex.Pattern


class RegexEventParser : EventParser {
    override fun parseEvent(event: String?): Event {
        val regex = "(\"[^\"]+\")|\\S+"
        val pattern = Pattern.compile(regex, Pattern.MULTILINE)
        val matcher = pattern.matcher(event)
        val fields = ArrayList<Any>()
        while (matcher.find()) {
            fields.add(matcher.group(0))
        }
        val timestamp = LocalDateTime.parse(matcher.group(0), formatter).atOffset(ZoneOffset.UTC)
        val id = EventId.of(matcher.group(1))
        val protocol = Protocol.valueOf(matcher.group(2))
        val activity = Activity(matcher.group(3), matcher.group(5))
        return Event(timestamp, id, protocol, activity)
    }
}
