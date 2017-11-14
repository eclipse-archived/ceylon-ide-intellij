/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.ide.intellij.model {
    IdeaCeylonProject
}
import javax.swing {
    ...
}

"A panel to modify settings in .ceylon/config"
shared interface CeylonConfigForm {

    shared formal JPanel panel;

    "Applies the user settings to the module's config."
    shared formal void apply(IdeaCeylonProject project);

    "Checks if the settings were modified by the user.
     Returns true if the settings were modified."
    shared formal Boolean isModified(IdeaCeylonProject project);

    "Loads the setting from a .ceylon/config file."
    shared formal void load(IdeaCeylonProject project);
}
