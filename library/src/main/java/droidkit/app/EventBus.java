package droidkit.app;

import android.support.annotation.NonNull;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import droidkit.annotation.OnEvent;
import droidkit.concurrent.ExecQueue;
import droidkit.log.Logger;
import droidkit.util.DynamicException;
import droidkit.util.DynamicMethod;

/**
 * @author Daniel Serdyukov
 */
public abstract class EventBus {

    private static final String BUS = "$Bus";

    private static final Map<Object, List<EventHandler>> SUBSCRIBERS = new ConcurrentHashMap<>();

    private static final Map<Class<?>, List<EventHandler>> HANDLERS = new ConcurrentHashMap<>();

    public static void register(@NonNull Object subscriber) {
        try {
            DynamicMethod.invokeStatic(subscriber.getClass().getName() + BUS, "register", subscriber);
        } catch (DynamicException e) {
            tryRegisterAtRuntime(subscriber);
        }
    }

    public static void unregister(@NonNull Object subscriber) {
        try {
            DynamicMethod.invokeStatic(subscriber.getClass().getName() + BUS, "unregister", subscriber);
        } catch (DynamicException e) {
            tryUnregisterAtRuntime(subscriber);
        }
    }

    public static void post(@NonNull Object event) {
        final List<EventHandler> handlers = HANDLERS.get(event.getClass());
        if (handlers != null && !handlers.isEmpty()) {
            for (final EventHandler handler : handlers) {
                handler.onEvent(event);
            }
        } else {
            Logger.error("Dead event: %s", event);
        }
    }

    public static void post(@NonNull ExecQueue queue, @NonNull final Object event) {
        final List<EventHandler> handlers = HANDLERS.get(event.getClass());
        if (handlers != null && !handlers.isEmpty()) {
            queue.invoke(new Runnable() {
                @Override
                public void run() {
                    for (final EventHandler handler : handlers) {
                        handler.onEvent(event);
                    }
                }
            });
        } else {
            Logger.error("Dead event: %s", event);
        }
    }

    /**
     * @hide
     */
    protected static void register(@NonNull Object subscriber, @NonNull Class<?> eventType,
                                   @NonNull EventHandler handler) {
        // Link handler with subscriber
        List<EventHandler> handlers = SUBSCRIBERS.get(subscriber);
        if (handlers == null) {
            handlers = new CopyOnWriteArrayList<>();
            SUBSCRIBERS.put(subscriber, handlers);
        }
        handlers.add(handler);
        // Link handler with event type
        handlers = HANDLERS.get(eventType);
        if (handlers == null) {
            handlers = new CopyOnWriteArrayList<>();
            HANDLERS.put(eventType, handlers);
        }
        handlers.add(handler);
    }

    /**
     * @hide
     */
    protected static void unregister(@NonNull Object subscriber, @NonNull Class<?> eventType) {
        final List<EventHandler> subscriberHandlers = SUBSCRIBERS.get(subscriber);
        final List<EventHandler> eventHandlers = HANDLERS.get(eventType);
        if (subscriberHandlers != null && eventHandlers != null) {
            eventHandlers.removeAll(subscriberHandlers);
        }
    }

    private static void tryRegisterAtRuntime(@NonNull final Object subscriber) {
        final Class<?> type = subscriber.getClass();
        final List<Method> methods = DynamicMethod.annotatedWith(type, OnEvent.class);
        for (final Method method : methods) {
            final Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1) {
                throw new IllegalArgumentException("Invalid method signature, " +
                        "expected parameters count = 1 (" + method + ")");
            }
            register(subscriber, parameters[0], new EventHandler() {
                @Override
                public void onEvent(@NonNull Object event) {
                    try {
                        DynamicMethod.invoke(subscriber, method, event);
                    } catch (DynamicException e) {
                        Logger.error(e);
                    }
                }
            });
        }
    }

    private static void tryUnregisterAtRuntime(@NonNull Object subscriber) {
        final Class<?> type = subscriber.getClass();
        final List<Method> methods = DynamicMethod.annotatedWith(type, OnEvent.class);
        for (final Method method : methods) {
            final Class<?>[] parameters = method.getParameterTypes();
            if (parameters.length != 1) {
                throw new IllegalArgumentException("Invalid method signature, " +
                        "expected parameters count = 1 (" + method + ")");
            }
            unregister(subscriber, parameters[0]);
        }
    }

}
