package org.yah.tools.opencl.cmdqueue;

import org.lwjgl.BufferUtils;
import org.lwjgl.PointerBuffer;

public class CLEventsBuffer {
    private static final int MIN_EVENTS_CAPACITY = 10;

    public static final CLEventsBuffer EMPTY_PARAM = new CLEventsBuffer();

    private final PointerBuffer eventBuffer = BufferUtils.createPointerBuffer(1).limit(0);

    private final PointerBuffer eventWaitListBuffer;

    public CLEventsBuffer() {
        this(10);
    }

    public CLEventsBuffer(int capacity) {
        eventWaitListBuffer = PointerBuffer.allocateDirect(capacity).limit(0);
    }

    public CLEventsBuffer reset() {
        eventWaitListBuffer.limit(0);
        eventBuffer.limit(0);
        return this;
    }

    public CLEventsBuffer waitForEvent(long event) {
        if (event == 0)
            eventWaitListBuffer.position(0).limit(0);
        else
            eventWaitListBuffer.position(0).put(event).flip();
        return this;
    }

    public CLEventsBuffer waitForEvents(long... events) {
        eventWaitListBuffer.position(0).limit(events.length);
        for (long event : events) {
            if (event > 0)
                eventWaitListBuffer.put(event);
        }
        eventWaitListBuffer.flip();
        return this;
    }

    public CLEventsBuffer dontWaitForEvents() {
        eventWaitListBuffer.position(0).limit(0);
        return this;
    }

    public CLEventsBuffer requestEvent() {
        this.eventBuffer.position(0).limit(1);
        return this;
    }

    protected long flushEvent() {
        if (eventBuffer.hasRemaining())
            return eventBuffer.get();
        return 0;
    }

    PointerBuffer getEventWaitListBuffer() {
        if (eventWaitListBuffer.hasRemaining())
            return eventWaitListBuffer;
        return null;
    }

    PointerBuffer getEventBuffer() {
        if (eventBuffer.hasRemaining())
            return eventBuffer;
        return null;
    }
}
