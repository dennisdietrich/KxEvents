package dev.dcdietrich.events

public class EventManager {
    private val events = HashSet<EventBase<*, *, *>>()

    public fun<TSource, TValue> createEvent(): Event<TSource, TValue> {
        return Event<TSource, TValue>().apply { events.add(this) }
    }

    public fun<TSource, TValue> createCancellableEvent(): CancellableEvent<TSource, TValue> {
        return CancellableEvent<TSource, TValue>().apply { events.add(this) }
    }

    public fun<TSource, TValue> raise(event: Event<TSource, TValue>, source: TSource, value: TValue): EventArgs<TSource, TValue> {
        validateOwnership(event)
        return EventArgs(source, value).apply { event.raise(this) }
    }

    public fun<TSource, TValue> raise(event: Event<TSource, TValue>, source: TSource, valueGenerator: () -> TValue): EventArgs<TSource, TValue>? {
        validateOwnership(event)

        if (event.handlerCount > 0)
            return raise(event, source, valueGenerator())

        return null
    }

    public fun<TSource, TValue> raise(event: CancellableEvent<TSource, TValue>, source: TSource, value: TValue): CancellableEventArgs<TSource, TValue> {
        validateOwnership(event)
        return CancellableEventArgs(source, value).apply { event.raise(this) }
    }

    public fun<TSource, TValue> raise(event: CancellableEvent<TSource, TValue>, source: TSource, valueGenerator: () -> TValue): CancellableEventArgs<TSource, TValue>? {
        validateOwnership(event)

        if (event.handlerCount > 0)
            return raise(event, source, valueGenerator())

        return null
    }

    private fun validateOwnership(event: EventBase<*, *, *>) {
        if (!events.contains(event))
            throw EventOwnershipException(event, this)
    }
}