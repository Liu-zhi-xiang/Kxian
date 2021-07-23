package com.gjmetal.star.mvp;



public interface IPresent<V> {
    void attachV(V view);

    void detachV();
}
