package dev.dcdietrich.tests.events

import dev.dcdietrich.events.*
import java.util.concurrent.atomic.*
import kotlin.reflect.*
import kotlin.reflect.full.*
import kotlin.reflect.jvm.*
import kotlin.test.*
import org.junit.jupiter.api.*

public class EventBaseTests {
    private lateinit var handlersProperty: KProperty1<EventBase<*, *, *>, *>

    @Suppress("UNCHECKED_CAST")
    private fun<TSource, TValue, TArgs : EventArgs<TSource, TValue>> EventBase<TSource, TValue, TArgs>.getHandlers(): AtomicReference<Array<(TArgs) -> Unit>>? {
        if (!::handlersProperty.isInitialized) {
            handlersProperty = EventBase::class.memberProperties.find { it.name == "handlers" }!!
            handlersProperty.isAccessible = true
        }

        return handlersProperty.get(this) as? AtomicReference<Array<(TArgs) -> Unit>>
    }

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

        val h1: (EventArgs<Any?, Any?>) -> Unit = { }
        val h2: (EventArgs<Any?, Any?>) -> Unit = { }

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

    @Test
    public fun plusAssign_ThreadSafety() {
        val event = Event<Any?, Any?>()
        val threads = mutableListOf<Thread>()

        val numberOfThreads = Runtime.getRuntime().availableProcessors()
        val iterationsPerThread = 10000

        for (i in 0 until numberOfThreads) {
            threads += Thread {
                val h: (EventArgs<Any?, Any?>) -> Unit = { }

                for (j in 1..iterationsPerThread)
                    event += h
            }

            threads[i].run()
        }

        for (i in 0 until numberOfThreads)
            threads[i].join()

        assertEquals(numberOfThreads * iterationsPerThread, event.getHandlers()!!.get().size)
    }

    @Test
    public fun minusAssign_ThreadSafety() {
        val event = Event<Any?, Any?>()
        val handlers = mutableListOf<(EventArgs<Any?, Any?>) -> Unit>()

        val numberOfThreads = Runtime.getRuntime().availableProcessors()
        val iterationsPerThread = 10000

        for (i in 0 until numberOfThreads) {
            handlers += { }

            for (j in 1..iterationsPerThread)
                event += handlers[i]
        }

        val threads = mutableListOf<Thread>()

        for (i in 0 until numberOfThreads) {
            threads += Thread {
                for (j in 1..iterationsPerThread)
                    event -= handlers[i]
            }

            threads[i].run()
        }

        for (i in 0 until numberOfThreads)
            threads[i].join()

        assertEquals(0, event.getHandlers()!!.get().size)
    }
}

public class EventTests {
    @Test
    public fun raise() {
        val source = Object()
        val event = Event<Any, String>()
        val args = mutableListOf<String>()

        event.raise(EventArgs(source, ""))
        assertEquals(0, args.size)

        val h1: (EventArgs<Any, String>) -> Unit = {
            assertSame(source, it.source)
            args += it.value + "h1"
        }
        event += h1

        event.raise(EventArgs(source, "e1"))
        assertEquals(1, args.size)
        assertEquals("e1h1", args[0])

        args.clear()
        val h2: (EventArgs<Any, String>) -> Unit = {
            assertSame(source, it.source)
            args += it.value + "h2"
        }
        event += h2

        event.raise(EventArgs(source, "e2"))
        assertEquals(2, args.size)
        assertEquals("e2h1", args[0])
        assertEquals("e2h2", args[1])

        args.clear()
        event += h1

        event.raise(EventArgs(source, "e3"))
        assertEquals(3, args.size)
        assertEquals("e3h1", args[0])
        assertEquals("e3h2", args[1])
        assertEquals("e3h1", args[2])
    }
}

public class CancellableEventTests {
    @Test
    public fun raise() {
        val source = Object()
        val event = CancellableEvent<Any, String>()
        val args = mutableListOf<String>()

        event.raise(CancellableEventArgs(source, ""))
        assertEquals(0, args.size)

        val h1: (CancellableEventArgs<Any, String>) -> Unit = {
            assertSame(source, it.source)
            args += it.value + "h1"
        }
        event += h1

        event.raise(CancellableEventArgs(source, "e1"))
        assertEquals(1, args.size)
        assertEquals("e1h1", args[0])

        args.clear()
        val h2: (CancellableEventArgs<Any, String>) -> Unit = {
            assertSame(source, it.source)
            args += it.value + "h2"
        }
        event += h2

        event.raise(CancellableEventArgs(source, "e2"))
        assertEquals(2, args.size)
        assertEquals("e2h1", args[0])
        assertEquals("e2h2", args[1])

        args.clear()
        event += h1

        event.raise(CancellableEventArgs(source, "e3"))
        assertEquals(3, args.size)
        assertEquals("e3h1", args[0])
        assertEquals("e3h2", args[1])
        assertEquals("e3h1", args[2])
    }

    @Test
    public fun raise_cancel() {
        val source = Object()
        val event = CancellableEvent<Any, String>()
        val args = mutableListOf<String>()

        val h1: (CancellableEventArgs<Any, String>) -> Unit = {
            assertSame(source, it.source)
            it.cancelled = true
            args += it.value + "h1"
        }
        event += h1

        event.raise(CancellableEventArgs(source, "e1"))
        assertEquals(1, args.size)
        assertEquals("e1h1", args[0])

        args.clear()
        val h2: (CancellableEventArgs<Any, String>) -> Unit = {
            assertSame(source, it.source)
            args += it.value + "h2"
        }
        event += h2

        event.raise(CancellableEventArgs(source, "e2"))
        assertEquals(1, args.size)
        assertEquals("e2h1", args[0])
    }
}