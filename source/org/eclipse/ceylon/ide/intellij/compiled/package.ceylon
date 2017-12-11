/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
"This package is responsible for handling binary declarations in .car files:

 * filtering out internal classes
 * changing labels and icons to reflect the original Ceylon declaration
 * associating original source declarations
 "
shared package org.eclipse.ceylon.ide.intellij.compiled;
