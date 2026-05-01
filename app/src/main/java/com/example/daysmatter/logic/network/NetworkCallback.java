package com.example.daysmatter.logic.network;


public interface NetworkCallback<T> {
    void onSuccess(T data);
    void onError(Throwable t);
}
