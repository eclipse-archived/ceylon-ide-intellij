package org.intellij.plugins.ceylon.ide.ceylonCode.model;

import java.util.concurrent.Callable;

public class ConcurrencyManagerForJava {
    public static <T> T withAlternateResolution(Callable<T> func) {
        return (T) concurrencyManagerForJava_.get_().withAlternateResolution((Callable<Object>)func);
    }

    public static Object withUpToDateIndexes(Callable<Object> func) {
        return concurrencyManagerForJava_.get_().withUpToDateIndexes(func);
    }

    public static Object outsideDumbMode(Callable<Object> func) {
        return concurrencyManagerForJava_.get_().outsideDumbMode(func);
    }
}
