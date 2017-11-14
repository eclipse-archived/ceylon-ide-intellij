/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
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
