import com.intellij {
    CommonBundle
}

import java.lang.ref {
    SoftReference
}
import java.util {
    ResourceBundle
}

shared class CeylonBundle {

    static variable SoftReference<ResourceBundle>? bundleRef = null;

    shared static String message(String key, Object* params) {
        ResourceBundle bundle;
        if (exists cached = bundleRef?.get()) {
            bundle =  cached;
        }
        else {
            bundle = ResourceBundle.getBundle("messages.CeylonBundle");
            bundleRef = SoftReference(bundle);
        }
        return CommonBundle.message(bundle, key, params);
    }

    new () {}

}
