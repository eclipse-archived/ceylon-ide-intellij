/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
native("jvm")
module org.eclipse.ceylon.ide.intellij "current" {
    shared import java.base "7";
    shared import org.eclipse.ceylon.ide.common "1.3.4-SNAPSHOT";
    shared import ceylon.tool.converter.java2ceylon "1.3.4-SNAPSHOT";
    shared import com.intellij.openapi "current";
    shared import com.intellij.idea "current";
    shared import org.jetbrains.plugins.gradle "current";
    shared import org.intellij.groovy "current";
    shared import org.intellij.maven "current";
    shared import org.jdom "current";
    shared import maven:com.google.guava:"guava" "19.0";
    import maven:com.intellij:"forms_rt" "7.0.3";
    import maven:"commons-lang":"commons-lang" "2.6";
    import com.github.rjeschke.txtmark "0.13";
    shared import java.desktop "7";
    import java.compiler "7";
    shared import jdk.tools "current";
}
