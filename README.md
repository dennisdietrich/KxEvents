# KxEvents
An implementation of events for Kotlin loosely based on those in .NET.
## Goals
* Easy to use
* Thread-safe (preferably using lock-free implementation for adding/removing event handlers)
* Proper encapsulation: Handler list can only be edited through method calls, events can only be raised by their owner
## Non-goals
* Java interop (this may change in the future)
## Usage Example
``` Kotlin
class WithEvents {
    private val eventManager = EventManager()

    val testEvent = eventManager.createEvent<WithEvents, String>()

    fun raiseEvent() {
        eventManager.raise(testEvent, this, "Hello Events!")
    }
}
```
``` Kotlin
fun main() {
    with(WithEvents()) {
        testEvent += { e ->
            println(e.source)
            println(e.value)
        }
        raiseEvent()
    }
}
```