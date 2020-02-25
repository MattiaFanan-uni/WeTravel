package com.gruppo3.wetravel.Persistence;

/**
 * Represents some details or preferences of a geo point.
 */
public interface Event<T> {
    /**
     * @return The details of this point.
     */
    T getContent();

    /**
     * @return The position of this point.
     */
    GPSPosition getPosition();
}