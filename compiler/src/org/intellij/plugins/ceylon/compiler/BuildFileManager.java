package org.intellij.plugins.ceylon.compiler;

import com.redhat.ceylon.compiler.java.tools.CeyloncFileManager;
import com.sun.tools.javac.file.RelativePath;
import com.sun.tools.javac.util.Context;
import org.jetbrains.jps.incremental.CompileContext;

import javax.tools.FileObject;
import javax.tools.JavaFileObject;
import java.io.IOException;
import java.nio.charset.Charset;

/**
 * TODO
 */
public class BuildFileManager extends CeyloncFileManager {
    private final CompileContext context;

    public BuildFileManager(Context context, boolean register, Charset charset, CompileContext ctx) {
        super(context, register, charset);
        this.context = ctx;
    }

    @Override
    protected String getCurrentWorkingDir() {
        return super.getCurrentWorkingDir();
    }

    @Override
    protected JavaFileObject getFileForOutput(Location location, RelativePath.RelativeFile fileName, FileObject sibling) throws IOException {
        return super.getFileForOutput(location, fileName, sibling);
    }
}
