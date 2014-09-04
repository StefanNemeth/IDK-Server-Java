package org.stevewinfield.suja.idk.game.event.impl

import org.junit.Test
import org.stevewinfield.suja.idk.game.event.Event
import org.stevewinfield.suja.idk.game.event.IEventListener

import java.util.concurrent.CountDownLatch

import static org.junit.Assert.assertEquals

class EventBusTest {
    private final EventBus bus = new EventBus()
    private final CountDownLatch latch = new CountDownLatch(2)

    @Test
    void testNestedEvents() {
        bus.registerListener(FirstEvent, {
            bus.post new SecondEvent()
            assertEquals 1, latch.getCount()
            latch.countDown()
        } as IEventListener)
        bus.registerListener(SecondEvent, {
            latch.countDown()
        } as IEventListener)
        bus.post new FirstEvent()
        assertEquals 0, latch.getCount()
    }

    static class FirstEvent extends Event {
    }

    static class SecondEvent extends Event {
    }
}
