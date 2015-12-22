package org.intellij.plugins.ceylon.compiler;

import com.intellij.openapi.util.io.FileUtil;
import org.jetbrains.jps.incremental.CompileContext;
import org.jetbrains.jps.incremental.Utils;
import org.jetbrains.jps.incremental.messages.BuildMessage;
import org.jetbrains.jps.incremental.messages.CompilerMessage;

import com.redhat.ceylon.javax.tools.Diagnostic;
import com.redhat.ceylon.javax.tools.DiagnosticListener;
import com.redhat.ceylon.javax.tools.JavaFileObject;
import java.io.File;

/**
 * Reports errors and warning outputted by the compiler to the IDE.
 */
public class CompileErrorReporter implements DiagnosticListener<JavaFileObject> {
    private final CompileContext ctx;
    private boolean errorReported;

    public CompileErrorReporter(CompileContext ctx) {
        this.ctx = ctx;
    }

    // Heavily stolen from JavaBuilder
    public void report(Diagnostic<? extends JavaFileObject> diagnostic) {
        errorReported = true;
        final CompilerMessage.Kind kind;
        switch (diagnostic.getKind()) {
            case ERROR:
                kind = BuildMessage.Kind.ERROR;
                break;
            case MANDATORY_WARNING:
            case WARNING:
                kind = BuildMessage.Kind.WARNING;
                break;
            case NOTE:
            default:
                kind = BuildMessage.Kind.INFO;
        }
        final JavaFileObject source = diagnostic.getSource();
        File sourceFile = null;
        try {
            sourceFile = source != null ? Utils.convertToFile(source.toUri()) : null;
        } catch (Exception e) {
            // LOG.info(e);
        }
        final String srcPath = sourceFile != null ? FileUtil.toSystemIndependentName(sourceFile.getPath()) : null;
        String message = diagnostic.getMessage(null);

        ctx.processMessage(
                new CompilerMessage("ceylon", kind, message, srcPath, diagnostic.getStartPosition(),
                        diagnostic.getEndPosition(), diagnostic.getPosition(), diagnostic.getLineNumber(),
                        diagnostic.getColumnNumber()));
    }

    public void failed() {
        if (!errorReported) {
            ctx.processMessage(new CompilerMessage("ceylon", BuildMessage.Kind.ERROR, "Unknown compiler error"));
        }
    }
}
