/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
"Contains information that helps determining if a .class was compiled by the Ceylon compiler,
 and if it should be ignored."
shared class CeylonBinaryData(timestamp, ceylonBinary, ceylonIgnore, inner) {
    "To be compared to a VirtualFile's timestamp to check if the current data is outdated."
    shared Integer timestamp;
    "Does this class have a `@Ceylon` annotation?"
    shared Boolean ceylonBinary;
    "Does this class have a `@Ignore` annotation?"
    shared Boolean ceylonIgnore;
    "Is this an inner/anonymous class?"
    shared Boolean inner;

    "Was this class compiled by the Ceylon compiler?"
    shared Boolean ceylonCompiledFile => ceylonBinary || ceylonIgnore;
}