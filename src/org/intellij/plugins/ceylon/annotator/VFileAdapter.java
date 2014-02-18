package org.intellij.plugins.ceylon.annotator;

import com.intellij.openapi.vfs.VirtualFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VFileAdapter implements com.redhat.ceylon.compiler.typechecker.io.VirtualFile {
    private VirtualFile delegate;

    public VFileAdapter(VirtualFile delegate) {
        this.delegate = delegate;
    }

    @Override
    public boolean isFolder() {
        return delegate.isDirectory();
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public String getPath() {
        return delegate.getPath();
    }

    @Override
    public InputStream getInputStream() {
        try {
            return delegate.getInputStream();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<com.redhat.ceylon.compiler.typechecker.io.VirtualFile> getChildren() {
        final VirtualFile[] delegateChildren = delegate.getChildren();
        final List<com.redhat.ceylon.compiler.typechecker.io.VirtualFile> children = new ArrayList<>(delegateChildren.length);
        for (VirtualFile delegateChild : delegateChildren) {
            children.add(new VFileAdapter(delegateChild));
        }
        return children;
    }
}
