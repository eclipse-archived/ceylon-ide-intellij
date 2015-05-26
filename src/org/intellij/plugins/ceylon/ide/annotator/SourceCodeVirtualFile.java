package org.intellij.plugins.ceylon.ide.annotator;

import com.intellij.psi.PsiFile;
import com.redhat.ceylon.compiler.typechecker.io.VirtualFile;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;

public class SourceCodeVirtualFile implements com.redhat.ceylon.compiler.typechecker.io.VirtualFile {

    private final PsiFile file;
    private final InputStream inputStream;

    public SourceCodeVirtualFile(PsiFile file) {
        this.file = file;
        inputStream = new ByteArrayInputStream(file.getText().getBytes());
    }

    @Override
    public boolean isFolder() {
        return false;
    }

    @Override
    public String getName() {
        return file.getName();
    }

    @Override
    public String getPath() {
        return file.getVirtualFile().getCanonicalPath();
    }

    @Override
    public InputStream getInputStream() {
        return inputStream;
    }

    @Override
    public List<com.redhat.ceylon.compiler.typechecker.io.VirtualFile> getChildren() {
        return Collections.emptyList();
    }

    @Override
    public int hashCode() {
        return getPath().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof com.redhat.ceylon.compiler.typechecker.io.VirtualFile) {
            return ((com.redhat.ceylon.compiler.typechecker.io.VirtualFile) obj).getPath().equals(getPath());
        }
        else {
            return super.equals(obj);
        }
    }

    @Override
    public int compareTo(VirtualFile o) {
        return 0;
    }
}
