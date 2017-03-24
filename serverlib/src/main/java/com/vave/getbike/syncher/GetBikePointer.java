package com.vave.getbike.syncher;

/**
 * Created by sivanookala on 26/10/16.
 */

public class GetBikePointer<T> {
    T t;

    public GetBikePointer() {
        this.t = null;
    }

    public GetBikePointer(T t) {
        this.t = t;
    }

    public T getValue() {
        return t;
    }

    public void setValue(T t) {
        this.t = t;
    }
}
