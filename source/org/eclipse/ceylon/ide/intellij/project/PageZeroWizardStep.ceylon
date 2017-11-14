/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.ide.util.projectWizard {
    ModuleWizardStep
}

"Page 0: settings not stored in CeylonConfig."
shared class PageZeroWizardStep(CeylonModuleBuilder moduleBuilder) extends ModuleWizardStep() {

    value step = PageZero();

    component => step.panel;

    updateDataModel()
            => moduleBuilder.setPageZero(step);
}
