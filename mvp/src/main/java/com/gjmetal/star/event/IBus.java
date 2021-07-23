package com.gjmetal.star.event;
public interface IBus {
    void register(Object object);

    void unregister(Object object);

    void post(Object object);

    void postSticky(Object object);

    void removeAllStickyEvents();

    void cancelEventDelivery(Object object);

    boolean isRegistered(Object object);

    void hasSubscriberForEvent(Class<?> eventClass);

    void getLogger();
}
