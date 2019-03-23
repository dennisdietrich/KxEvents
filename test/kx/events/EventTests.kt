package kx.events

import java.util.concurrent.atomic.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.*
import kotlin.test.*
import org.junit.jupiter.api.*

@Suppress("UNCHECKED_CAST")
private fun<TSource, TValue, TArgs : EventArgs<TSource, TValue>> EventBase<TSource, TValue, TArgs>.getHandlers(): AtomicReference<Array<(TArgs) -> Unit>>? {
    val property = EventBase::class.memberProperties.find { it.name == "handlers" }!!
    property.isAccessible = true
    return property.get(this) as? AtomicReference<Array<(TArgs) -> Unit>>
}

public class EventBaseTests {
    private class Event<TSource, TValue> : EventBase<TSource, TValue, EventArgs<TSource, TValue>>() {
        internal override fun raise(args: EventArgs<TSource, TValue>) {
        }
    }

    @Test
    public fun constructor() {
        val event = Event<Any?, Any?>()

        val handlersReference = event.getHandlers()
        assertNotNull(handlersReference)

        val handlers = handlersReference.get()
        assertNotNull(handlers)
        assertEquals(0, handlers.size)
    }

    @Test
    public fun plusAssign() {
        val event = Event<Any?, Any?>()

        val h1: (EventArgs<Any?, Any?>) -> Unit = { }
        val h2: (EventArgs<Any?, Any?>) -> Unit = { }

        event += h1
        var handlers = event.getHandlers()!!.get()!!
        assertEquals(1, handlers.size)
        assertSame(h1, handlers[0])

        event += h2
        handlers = event.getHandlers()!!.get()!!
        assertEquals(2, handlers.size)
        assertSame(h1, handlers[0])
        assertSame(h2, handlers[1])

        event += h1
        handlers = event.getHandlers()!!.get()!!
        assertEquals(3, handlers.size)
        assertSame(h1, handlers[0])
        assertSame(h2, handlers[1])
        assertSame(h1, handlers[2])
    }

    @Test
    public fun minusAssign() {
        val event = Event<Any?, Any?>()

        val h1: (EventArgs<Any?, Any?>) -> Unit = { _ -> run { } }
        val h2: (EventArgs<Any?, Any?>) -> Unit = { _ -> run { } }

        event -= h1
        var handlers = event.getHandlers()!!.get()!!
        assertEquals(0, handlers.size)

        event += h1
        event += h2
        event += h1
        event += h2

        event -= h2
        handlers = event.getHandlers()!!.get()!!
        assertEquals(3, handlers.size)
        assertSame(h1, handlers[0])
        assertSame(h1, handlers[1])
        assertSame(h2, handlers[2])

        event -= { }
        event -= h1
        handlers = event.getHandlers()!!.get()!!
        assertEquals(2, handlers.size)
        assertSame(h1, handlers[0])
        assertSame(h2, handlers[1])

        event -= h2
        handlers = event.getHandlers()!!.get()!!
        assertEquals(1, handlers.size)
        assertSame(h1, handlers[0])

        event -= h1
        handlers = event.getHandlers()!!.get()!!
        assertEquals(0, handlers.size)
    }
}