# KxEvents
An implementation of events for Kotlin loosely based on those in .NET.
## Goals
* Easy to use
* Thread-safe when adding and removing event handlers (preferably using lock-free implementation)
* Proper encapsulation: Handler list can only be modified through method calls; events can only be raised by their owner
## Non-goals
* Java interop (this may change in the future)
## Usage Example
``` Kotlin
class WithEvents {
    private val eventManager = EventManager(this)

    val testEvent = eventManager.createEvent<String>()

    fun raiseEvent() {
        eventManager.raise(testEvent, "Hello Events!")
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