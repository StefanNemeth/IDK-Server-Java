package org.stevewinfield.suja.idk.game.event.impl

import org.junit.Test
import org.stevewinfield.suja.idk.game.event.Event
import org.stevewinfield.suja.idk.game.event.IEventListener

import static org.junit.Assert.fail

class UnregisteringListenerTest {
    private final EventBus bus = new EventBus();

    @Test
    void testPriority() {
        def eventListener = {
            fail("Event listener wasn't unregistered")
        } as IEventListener
        bus.registerListener(TestEvent, eventListener)
        bus.unregisterListener(eventListener)
        bus.post(new TestEvent());
    }

    static class TestEvent extends Event {
    }
}
