package org.stevewinfield.suja.idk.game.event.impl;

import org.junit.Test;
import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.event.IEventListener;

import java.util.concurrent.CountDownLatch;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class EventBusAutomaticDetectionTest {
    private final EventBus bus = new EventBus();
    private final CountDownLatch latch = new CountDownLatch(2);

    @Test
    public void testAutomaticDetection() {
        bus.registerListener(new IEventListener<FirstEvent>() {
            @Override
            public void onEvent(FirstEvent event) {
                bus.post(new SecondEvent());
                assertEquals(1, latch.getCount());
                latch.countDown();
            }
        });
        bus.registerListener(new SecondEventListener());
        bus.post(new FirstEvent());
        assertEquals(0, latch.getCount());
    }

    @Test(expected=UnsupportedOperationException.class)
    public void failWhenNoGenericInterface() {
        bus.registerListener(new IEventListener() {
            @Override
            public void onEvent(Event event) {
                assertTrue(false);
            }
        });
    }

    static class FirstEvent extends Event {
    }

    static class SecondEvent extends Event {
        public boolean isTrue() {
            return true;
        }
    }

    private class SecondEventListener implements IEventListener<SecondEvent> {

        @Override
        public void onEvent(SecondEvent event) {
            assertTrue(event.isTrue());
            latch.countDown();
        }
    }
}
