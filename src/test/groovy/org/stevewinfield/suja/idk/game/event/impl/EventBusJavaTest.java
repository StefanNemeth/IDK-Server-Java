package org.stevewinfield.suja.idk.game.event.impl;

import org.junit.Test;
import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.event.IEventListener;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.*;

public class EventBusJavaTest {
    private final EventBus bus = new EventBus();
    private final CountDownLatch latch = new CountDownLatch(2);

    @Test
    public void testNestedEvents() {
        bus.registerListener(FirstEvent.class, new IEventListener<FirstEvent>() {
            @Override
            public void onEvent(FirstEvent event) {
                bus.post(new SecondEvent());
                assertEquals(1, latch.getCount());
                latch.countDown();
            }
        });
        bus.registerListener(SecondEvent.class, new SecondEventListener());
        bus.post(new FirstEvent());
        assertEquals(0, latch.getCount());
    }

    static class FirstEvent extends Event {
    }

    static class SecondEvent extends Event {
        public boolean isTrue() {
            return true;
        }
    }

    private class SecondEventListener implements IEventListener<EventBusJavaTest.SecondEvent> {

        @Override
        public void onEvent(EventBusJavaTest.SecondEvent event) {
            assertTrue(event.isTrue());
            latch.countDown();
        }
    }
}
