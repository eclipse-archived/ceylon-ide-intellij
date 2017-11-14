/********************************************************************************
 * Copyright (c) {date} Red Hat Inc. and/or its affiliates and others
 *
 * This program and the accompanying materials are made available under the 
 * terms of the Apache License, Version 2.0 which is available at
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * SPDX-License-Identifier: Apache-2.0 
 ********************************************************************************/
import com.intellij.openapi.compiler {
    CompileContext,
    CompilerMessageCategory
}

import java.io {
    Writer
}
import java.lang {
    CharArray,
    Str=String
}

shared class MessageWriter(CompileContext context) extends Writer() {

    write(CharArray cbuf, Integer off, Integer len)
            => context.addMessage(CompilerMessageCategory.information,
                    Str(cbuf, off, len).string, null, -1, -1);

    shared actual void flush() {}

    shared actual void close() {}

}
