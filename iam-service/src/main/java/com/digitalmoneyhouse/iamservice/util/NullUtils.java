package com.digitalmoneyhouse.iamservice.util;

import java.util.function.Consumer;

public class NullUtils {
    public static <T> void updateIfPresent(Consumer<T> consumer, T value){
        if (value != null){
            consumer.accept(value);
        }
    }
}
