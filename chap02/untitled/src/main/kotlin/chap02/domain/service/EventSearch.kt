package chap02.domain.service

import chap02.domain.entity.Event
import chap02.domain.vo.ParsePolicyType

class EventSearch {
    fun retrieveEvents(unparsedEvents: List<String?>, policyType: ParsePolicyType?): List<Event> {
        val parsedEvents= mutableListOf<Event>()
        unparsedEvents.stream().forEach { event: String? ->
            parsedEvents.add(
                Event.parsedEvent(event, policyType)
            )
        }
        return parsedEvents
    }
}