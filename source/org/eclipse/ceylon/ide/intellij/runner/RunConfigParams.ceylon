/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import org.eclipse.ceylon.common {
    Backend
}

class RunConfigParams(moduleName, packageName, topLevel, backend) {

    shared String moduleName;
    shared String packageName;
    shared String topLevel;
    shared Backend backend;

    shared actual Boolean equals(Object that) {
        if (is RunConfigParams that) {
            return this.moduleName == that.moduleName
                && this.packageName == that.packageName
                && this.topLevel == that.topLevel
                && this.backend == that.backend;
        }
        else {
            return false;
        }
    }

    hash => moduleName.hash + packageName.hash + topLevel.hash + backend.hash;

}