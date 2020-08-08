package dev.dcdietrich.events

public class EventManager<TSource : Any>(val source: TSource) {
    private val events = HashSet<EventBase<*, *, *>>()

    public fun<TValue> createEvent(): Event<TSource, TValue> {
        return Event<TSource, TValue>().apply { events.add(this) }
    }

    public fun<TValue> createCancellableEvent(): CancellableEvent<TSource, TValue> {
        return CancellableEvent<TSource, TValue>().apply { events.add(this) }
    }

    public fun<TValue> raise(event: Event<TSource, TValue>, value: TValue): EventArgs<TSource, TValue> {
        validateOwnership(event)
        return EventArgs(source, value).apply { event.raise(this) }
    }

    public fun<TValue> raise(event: Event<TSource, TValue>, valueGenerator: () -> TValue): EventArgs<TSource, TValue>? {
        validateOwnership(event)

        if (event.handlerCount > 0)
            return raise(event, valueGenerator())

        return null
    }

    public fun<TValue> raise(event: CancellableEvent<TSource, TValue>, value: TValue): CancellableEventArgs<TSource, TValue> {
        validateOwnership(event)
        return CancellableEventArgs(source, value).apply { event.raise(this) }
    }

    public fun<TValue> raise(event: CancellableEvent<TSource, TValue>, valueGenerator: () -> TValue): CancellableEventArgs<TSource, TValue>? {
        validateOwnership(event)

        if (event.handlerCount > 0)
            return raise(event, valueGenerator())

        return null
    }

    private fun validateOwnership(event: EventBase<*, *, *>) {
        if (!events.contains(event))
            throw EventOwnershipException(event, this)
    }
}