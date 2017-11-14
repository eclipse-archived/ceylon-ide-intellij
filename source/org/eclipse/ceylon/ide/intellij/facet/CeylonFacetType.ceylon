/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.facet {
    Facet,
    FacetType
}
import com.intellij.openapi.\imodule {
    JavaModuleType,
    Module,
    ModuleType
}

import org.eclipse.ceylon.ide.intellij.project {
    CeylonModuleType
}
import org.eclipse.ceylon.ide.intellij.util {
    icons
}

shared class CeylonFacetType()
        extends FacetType<CeylonFacet,CeylonFacetConfiguration>(CeylonFacet.id, "ceylon", "Ceylon") {

    createDefaultConfiguration() => CeylonFacetConfiguration();

    shared actual CeylonFacet createFacet(Module mod, String name,
        CeylonFacetConfiguration configuration, Facet<out Anything> underlyingFacet) {

        configuration.setModule(mod);
        return CeylonFacet(mod, name, configuration);
    }

    isSuitableModuleType(ModuleType<out Anything> moduleType)
            => moduleType is JavaModuleType
            || moduleType is CeylonModuleType
            ||"PLUGIN_MODULE" == moduleType.id;

    icon => icons.ceylon;
}
