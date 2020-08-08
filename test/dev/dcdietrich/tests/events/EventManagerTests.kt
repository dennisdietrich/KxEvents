package dev.dcdietrich.tests.events

import dev.dcdietrich.events.*
import kotlin.test.*
import org.junit.jupiter.api.*

public class EventManagerTests {
    @Test
    public fun constructor() {
        val manager = EventManager(this)
        assertSame(this, manager.source)
    }

    @Test
    public fun createEvent() {
        val manager = EventManager(this)
        assertNotSame(manager.createEvent<Any>(), manager.createEvent())
    }

    @Test
    public fun createCancellableEvent() {
        val manager = EventManager(this)
        assertNotSame(manager.createCancellableEvent<Any>(), manager.createCancellableEvent())
    }

    @Test
    public fun raise_Event() {
        val manager = EventManager(this)
        val event = manager.createEvent<Any?>()
        var raised = false

        event += { raised = true }
        manager.raise(event, null as Any?)

        assertTrue(raised)
    }

    @Test
    public fun raise_Event_WithGenerator() {
        val manager = EventManager(this)
        val event = manager.createEvent<Unit>()

        var generatorCalled = false
        val generatorCallback = { generatorCalled = true }
        manager.raise(event, generatorCallback)
        assertFalse(generatorCalled)

        var raised = false
        event += { raised = true }
        manager.raise(event, generatorCallback)
        assertTrue(raised)
        assertTrue(generatorCalled)
    }

    @Test
    public fun raise_CancellableEvent() {
        val manager = EventManager(this)
        val event = manager.createCancellableEvent<Any?>()
        var raised = false

        event += { raised = true }
        manager.raise(event, null as Any?)

        assertTrue(raised)
    }

    @Test
    public fun raise_CancellableEvent_WithGenerator() {
        val manager = EventManager(this)
        val event = manager.createCancellableEvent<Unit>()

        var generatorCalled = false
        val generatorCallback = { generatorCalled = true }
        manager.raise(event, generatorCallback)
        assertFalse(generatorCalled)

        var raised = false
        event += { raised = true }
        manager.raise(event, generatorCallback)
        assertTrue(raised)
        assertTrue(generatorCalled)
    }

    @Test
    public fun raise_UnownedEvent() {
        val event   = Event<EventManagerTests, Any?>()
        val manager = EventManager(this)

        assertThrows<EventOwnershipException> { manager.raise(event, null as Any?) }
    }

    @Test
    public fun raise_UnownedCancellableEvent() {
        val event   = CancellableEvent<EventManagerTests, Any?>()
        val manager = EventManager(this)

        assertThrows<EventOwnershipException> { manager.raise(event, null as Any?) }
    }
}