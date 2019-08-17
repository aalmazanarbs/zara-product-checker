package com.gade.zaraproductchecker.util;

import java.util.Optional;
import java.util.function.Consumer;

public final class OptionalUtil {

    @SuppressWarnings("unused")
    public static <T> void ifPresentOrElse(final Optional<T> optional, final Consumer<? super T> action, Runnable emptyAction) {
        if (optional.isPresent()) {
            action.accept(optional.get());
        } else {
            emptyAction.run();
        }
    }
}
