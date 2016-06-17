package org.intellij.plugins.ceylon.ide.ceylonCode.util;

public class LogUtils {
    public static int retrieveHash(Object obj) {
        if (obj == null) {
            return 0;
        } else {
            return obj.hashCode();
        }
    }
}
