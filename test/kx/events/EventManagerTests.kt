package kx.events

import kotlin.test.*
import org.junit.jupiter.api.*

public class EventManagerTests {
    @Test
    public fun createEvent() {
        val manager = EventManager()
        assertNotSame(manager.createEvent<Any, Any>(), manager.createEvent())
    }

    @Test
    public fun createCancellableEvent() {
        val manager = EventManager()
        assertNotSame(manager.createCancellableEvent<Any, Any>(), manager.createCancellableEvent())
    }

    @Test
    public fun raise_Event() {
        val manager = EventManager()
        val event = manager.createEvent<Any?, Any?>()
        var raised = false

        event += { raised = true }
        manager.raise(event, null, null)

        assertTrue(raised)
    }

    @Test
    public fun raise_CancellableEvent() {
        val manager = EventManager()
        val event = manager.createCancellableEvent<Any?, Any?>()
        var raised = false

        event += { raised = true }
        manager.raise(event, null, null)

        assertTrue(raised)
    }

    @Test
    public fun raise_UnownedEvent() {
        val event   = Event<Any?, Any?>()
        val manager = EventManager()

        assertThrows<EventOwnershipException> { manager.raise(event, null, null) }
    }

    @Test
    public fun raise_UnownedCancellableEvent() {
        val event   = CancellableEvent<Any?, Any?>()
        val manager = EventManager()

        assertThrows<EventOwnershipException> { manager.raise(event, null, null) }
    }
}