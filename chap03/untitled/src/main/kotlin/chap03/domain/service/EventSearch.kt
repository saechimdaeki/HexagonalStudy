package chap03.domain.service

import chap03.domain.entity.Event
import chap03.domain.vo.ParsePolicyType

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