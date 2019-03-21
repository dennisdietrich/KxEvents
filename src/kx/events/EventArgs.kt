package kx.events

public open class EventArgs<TSource, TValue> public constructor(
    public val source: TSource,
    public val value: TValue)

public class CancellableEventArgs<TSource, TValue> public constructor(
    source: TSource,
    value: TValue,
    public var cancelled: Boolean = false
) : EventArgs<TSource, TValue>(source, value)