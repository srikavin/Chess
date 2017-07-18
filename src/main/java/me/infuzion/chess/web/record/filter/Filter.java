package me.infuzion.chess.web.record.filter;

public interface Filter<T> {
    boolean matches(T toFilter);
}
