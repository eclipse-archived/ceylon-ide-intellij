/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.navigation {
    ItemPresentationProvider,
    ColoredItemPresentation
}
import com.intellij.openapi.editor.colors {
    CodeInsightColors
}
import org.eclipse.ceylon.model.typechecker.model {
    ClassOrInterface,
    Scope,
    Declaration
}

import org.eclipse.ceylon.ide.intellij.util {
    icons
}

shared class DeclarationPresentationProvider()
        satisfies ItemPresentationProvider<DeclarationNavigationItem> {

    String nestedName(Declaration dec)
            => if (is ClassOrInterface type = dec.container)
            then nestedName(type) + "." + dec.name
            else dec.name;

    function locationAsString(Scope container)
            => "(``container.qualifiedNameString else "default package"``)";

    String nestedLocation(Declaration dec)
            => if (is ClassOrInterface type = dec.container)
            then nestedLocation(type)
            else locationAsString(dec.container);

    getPresentation(DeclarationNavigationItem item)
            => object satisfies ColoredItemPresentation {
        getIcon(Boolean unused) => icons.forDeclaration(item.declaration);
        locationString => nestedLocation(item.declaration);
        presentableText => nestedName(item.declaration);
        textAttributesKey
                => item.declaration.deprecated
                then CodeInsightColors.deprecatedAttributes
                else null;
    };
}
