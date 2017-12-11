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
    FacetManager,
    FacetTypeId,
    FacetTypeRegistry
}
import com.intellij.openapi.\imodule {
    Module
}

shared class CeylonFacet extends Facet<CeylonFacetConfiguration> {

    shared static FacetTypeId<CeylonFacet> id = FacetTypeId<CeylonFacet>("ceylon");

    shared static CeylonFacetType facetType {
        assert(is CeylonFacetType type = FacetTypeRegistry.instance.findFacetType(id));
        return type;
    }

    shared static CeylonFacet? forModule(Module mod)
            => FacetManager.getInstance(mod).getFacetByType(id);

    shared new (Module mod, String name, CeylonFacetConfiguration config)
        extends Facet<CeylonFacetConfiguration>(facetType, mod, name, config, null) {}
}
