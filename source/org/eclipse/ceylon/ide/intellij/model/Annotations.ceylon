/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
shared class Annotations {
    
    shared actual String string;
    shared String className;
    
    abstract new create(String string, String className) {
        this.string = string;
        this.className = className;
    }

    shared new attribute extends create("attribute",
        "org.eclipse.ceylon.compiler.java.metadata.Attribute") {}

    shared
    new annotationInstantiation extends create("annotationInstantiation",
        "org.eclipse.ceylon.compiler.java.metadata.AnnotationInstantiation") {}

    shared
    new annotationType extends create("annotationType",
        //work around bug in intelliJ's decompiler
        "ceylon.language.AnnotationAnnotation.annotation$") {}

    shared
    new \iclass extends create("clazz",
        "org.eclipse.ceylon.compiler.java.metadata.Class") {}

    shared
    new \iobject extends create("object",
        "org.eclipse.ceylon.compiler.java.metadata.Object") {}

    shared
    new method extends create("method",
        "org.eclipse.ceylon.compiler.java.metadata.Method") {}

    shared
    new container extends create("container",
        "org.eclipse.ceylon.compiler.java.metadata.Container") {}

    shared
    new localContainer extends create("localContainer",
        "org.eclipse.ceylon.compiler.java.metadata.LocalContainer") {}

    shared
    new ceylon extends create("ceylon",
        "org.eclipse.ceylon.compiler.java.metadata.Ceylon") {}

    shared
    new ignore extends create("ignore",
        "org.eclipse.ceylon.compiler.java.metadata.Ignore") {}

    shared
    new deprecated extends create("deprecated",
        "java.lang.Deprecated") {}

    shared
    new packageDescriptor extends create("packageDescriptor",
        "org.eclipse.ceylon.compiler.java.metadata.Package") {}

    shared
    new moduleDescriptor extends create("moduleDescriptor",
        "org.eclipse.ceylon.compiler.java.metadata.Module") {}

    shared
    new constructorName extends create("constructorName",
        "org.eclipse.ceylon.compiler.java.metadata.ConstructorName") {}
}
