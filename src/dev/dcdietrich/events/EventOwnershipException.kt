package dev.dcdietrich.events

import java.lang.*

public class EventOwnershipException constructor(
    public val event:   EventBase<*, *, *>,
    public val manager: EventManager
) : Exception("The event is not owned by the event manager.")