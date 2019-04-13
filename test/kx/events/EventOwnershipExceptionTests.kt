package kx.events

import kotlin.test.*
import org.junit.jupiter.api.*

public class EventOwnershipExceptionTests {
    @Test
    public fun constructor() {
        val event   = Event<Any, Any>()
        val manager = EventManager()

        val exception = EventOwnershipException(event, manager)
        assertSame(event, exception.event)
        assertSame(manager, exception.manager)
        assertEquals("The event is not owned by the event manager.", exception.message)
    }
}