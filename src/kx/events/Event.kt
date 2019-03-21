package kx.events

import java.util.concurrent.atomic.*

public abstract class EventBase<TSource, TValue, TArgs : EventArgs<TSource, TValue>> {
    protected val handlers = AtomicReference(arrayOf<(TArgs) -> Unit>())

    public operator fun plusAssign(handler: (TArgs) -> Unit) {
        do {
            val oldArray = handlers.get()!!
            val newArray = oldArray + handler
        } while (!handlers.compareAndSet(oldArray, newArray))
    }

    public operator fun minusAssign(handler: (TArgs) ->Unit) {
        do {
            val oldArray = handlers.get()!!
            val index    = oldArray.indexOf(handler)

            if (index < 0)
                return

            val newArray = Array<(TArgs) -> Unit>(oldArray.size - 1) {
                if (it < index)
                    oldArray[it]
                else
                    oldArray[it + 1]
            }
        } while (!handlers.compareAndSet(oldArray, newArray))
    }

    internal abstract fun raise(args: TArgs)
}

public class Event<TSource, TValue> : EventBase<TSource, TValue, EventArgs<TSource, TValue>>() {
    internal override fun raise(args: EventArgs<TSource, TValue>) {
        val currentHandlers = handlers.get()!!

        for (handler in currentHandlers)
            handler(args)
    }
}

public class CancellableEvent<TSource, TValue> : EventBase<TSource, TValue, CancellableEventArgs<TSource, TValue>>() {
    internal override fun raise(args: CancellableEventArgs<TSource, TValue>) {
        val currentHandlers = handlers.get()!!

        for (handler in currentHandlers) {
            handler(args)

            if (args.cancelled)
                return
        }
    }
}