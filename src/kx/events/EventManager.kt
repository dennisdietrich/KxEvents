package kx.events

public class EventManager {
    private val events = HashSet<EventBase<*, *, *>>()

    public fun<TSource, TValue> createEvent(): Event<TSource, TValue> {
        val event = Event<TSource, TValue>()
        events.add(event);
        return event;
    }

    public fun<TSource, TValue> createCancellableEvent(): CancellableEvent<TSource, TValue> {
        val event = CancellableEvent<TSource, TValue>()
        events.add(event)
        return event
    }

    public fun<TSource, TValue> raise(event: Event<TSource, TValue>, source: TSource, value: TValue): EventArgs<TSource, TValue> {
        validateOwnership(event)

        val args = EventArgs(source, value)
        event.raise(args)
        return args
    }

    public fun<TSource, TValue> raise(event: CancellableEvent<TSource, TValue>, source: TSource, value: TValue): CancellableEventArgs<TSource, TValue> {
        validateOwnership(event)

        val args = CancellableEventArgs(source, value)
        event.raise(args)
        return args
    }

    private fun validateOwnership(event: EventBase<* ,* ,*>) {
        if (!events.contains(event))
            throw EventOwnershipException(event, this)
    }
}