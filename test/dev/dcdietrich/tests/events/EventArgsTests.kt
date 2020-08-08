package dev.dcdietrich.tests.events

import dev.dcdietrich.events.*
import kotlin.test.*
import org.junit.jupiter.api.*

public class EventArgsTests {
    @Test
    public fun constructor() {
        val source = Object()
        val value  = Object()

        val eventArgs = EventArgs(source, value)
        assertEquals(source, eventArgs.source)
        assertEquals(value,  eventArgs.value)
    }
}

public class CancellableEventArgsTests {
    @Test
    public fun constructor() {
        val source = Object()
        val value  = Object()

        val eventArgs = CancellableEventArgs(source, value)
        assertEquals(source, eventArgs.source)
        assertEquals(value,  eventArgs.value)
        assertFalse(eventArgs.cancelled)
    }
}