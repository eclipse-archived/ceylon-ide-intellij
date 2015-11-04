package org.intellij.plugins.ceylon.ide.build;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.Writer;

public class MessageWriter extends Writer {

    private CompileContext context;

    public MessageWriter(CompileContext context) {
        this.context = context;
    }

    @Override
    public void write(@NotNull char[] cbuf, int off, int len) throws IOException {
        context.addMessage(CompilerMessageCategory.INFORMATION,
                new String(cbuf, off, len), null, -1, -1);
    }

    @Override
    public void flush() throws IOException {}

    @Override
    public void close() throws IOException {}
}
