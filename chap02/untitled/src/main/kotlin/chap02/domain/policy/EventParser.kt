package chap02.domain.policy

import chap02.domain.entity.Event
import java.time.ZoneId
import java.time.format.DateTimeFormatter

interface EventParser {
    val formatter: DateTimeFormatter
        get() = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS").withZone(ZoneId.of("UTC"))

    fun parseEvent(event: String?): Event?
}