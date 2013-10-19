package org.intellij.plugins.ceylon.parser;

import org.antlr.runtime.ANTLRStringStream;

public class MyCharStream extends ANTLRStringStream {

    public MyCharStream(CharSequence input) {
        super(input.toString());
    }
}