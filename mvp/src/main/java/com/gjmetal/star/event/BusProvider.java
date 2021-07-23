package com.gjmetal.star.event;

import org.greenrobot.eventbus.EventBus;

public class BusProvider implements IBus {

    private static BusProvider bus;

    public static BusProvider getBus() {
        if (bus == null) {
            synchronized (BusProvider.class) {
                if (bus == null) {
                    bus = new BusProvider();
                }
            }
        }
        return bus;
    }


    @Override
    public void register(Object object) {
        if(!isRegistered(object)){
            EventBus.getDefault().register(object);
        }
    }

    @Override
    public void unregister(Object object) {
        if(isRegistered(object)){
            EventBus.getDefault().unregister(object);
        }
    }

    @Override
    public void post(Object object) {
        EventBus.getDefault().post(object);
    }

    @Override
    public void postSticky(Object object) {
        EventBus.getDefault().postSticky(object);
    }

    @Override
    public void removeAllStickyEvents() {
        EventBus.getDefault().removeAllStickyEvents();
    }

    @Override
    public void cancelEventDelivery(Object object) {
        EventBus.getDefault().cancelEventDelivery(object);
    }

    @Override
    public boolean isRegistered(Object object) {
       return EventBus.getDefault().isRegistered(object);
    }

    @Override
    public void hasSubscriberForEvent(Class<?> eventClass) {
        EventBus.getDefault().hasSubscriberForEvent(eventClass);
    }

    @Override
    public void getLogger() {
        EventBus.getDefault().getLogger();
    }

}
