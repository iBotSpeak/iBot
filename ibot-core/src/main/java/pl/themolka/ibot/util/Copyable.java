package pl.themolka.ibot.util;

public interface Copyable<T> {
    T copy() throws Throwable;
}
