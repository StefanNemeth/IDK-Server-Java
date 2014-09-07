package org.stevewinfield.suja.idk.game.event.impl

import org.junit.Test
import org.stevewinfield.suja.idk.game.event.Event
import org.stevewinfield.suja.idk.game.event.EventPriority
import org.stevewinfield.suja.idk.game.event.IEventListener

import java.util.concurrent.CountDownLatch

import static org.junit.Assert.assertEquals

class EventPriorityTest {
    private final EventBus bus = new EventBus()
    private final CountDownLatch latch = new CountDownLatch(5)

    @Test
    void testPriority() {
        bus.registerListener(PriorityTestEvent, {
            assertEquals 5, latch.getCount()
            latch.countDown()
        } as IEventListener, EventPriority.LOWEST)

        bus.registerListener(PriorityTestEvent, {
            assertEquals 4, latch.getCount()
            latch.countDown()
        } as IEventListener, EventPriority.LOW)

        bus.registerListener(PriorityTestEvent, {
            assertEquals 3, latch.getCount()
            latch.countDown()
        } as IEventListener)

        bus.registerListener(PriorityTestEvent, {
            assertEquals 2, latch.getCount()
            latch.countDown()
        } as IEventListener, EventPriority.HIGH)

        bus.registerListener(PriorityTestEvent, {
            assertEquals 1, latch.getCount()
            latch.countDown()
        } as IEventListener, EventPriority.HIGHEST)

        bus.post new PriorityTestEvent()
        assertEquals 0, latch.getCount()
    }

    static class PriorityTestEvent extends Event {
    }
}

