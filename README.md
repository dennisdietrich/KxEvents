# KxEvents
An implementation of events for Kotlin similar to those in .NET.
## Goals
* Low overhead: Event objects are lazily initialized
* Thread-safe (preferably using lock-free implementation for adding/removing event handlers)
* Proper encapsulation: Handler list can only be edited through method calls, events can only be raised by their owner
## Non-goals
* Java interop (this may change in the future)
