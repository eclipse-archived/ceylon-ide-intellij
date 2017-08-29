import com.intellij {
    CommonBundle
}

import java.lang.ref {
    Reference,
    SoftReference
}
import java.util {
    ResourceBundle
}

import org.jetbrains.annotations {
    propertyKey
}

shared class CeylonBundle {

    static late Reference<ResourceBundle> bundleRef
            = SoftReference(ResourceBundle.getBundle("messages.CeylonBundle"));

    shared static String message(
            propertyKey { resourceBundle = "messages.CeylonBundle"; } String key,
            Object* params)
            => CommonBundle.message(bundleRef.get(), key, params);

    new () {}

}
