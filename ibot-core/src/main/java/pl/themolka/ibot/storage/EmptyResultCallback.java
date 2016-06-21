package pl.themolka.ibot.storage;

import com.mongodb.async.SingleResultCallback;

public class EmptyResultCallback<T> implements SingleResultCallback<T> {
    @Override
    public void onResult(T result, Throwable throwable) {
    }
}
