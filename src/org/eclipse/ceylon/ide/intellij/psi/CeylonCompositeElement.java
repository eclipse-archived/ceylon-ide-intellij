/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
package org.eclipse.ceylon.ide.intellij.psi;

import com.intellij.navigation.NavigationItem;
import com.intellij.psi.PsiElement;
import org.eclipse.ceylon.compiler.typechecker.tree.Node;

public interface CeylonCompositeElement extends PsiElement, NavigationItem {

    Node getCeylonNode();
}
