package org.intellij.plugins.ceylon.annotator;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class VFileAdapter implements com.redhat.ceylon.compiler.typechecker.io.VirtualFile {
    private VirtualFile delegate;

    public VFileAdapter(@NotNull VirtualFile delegate) {
        this.delegate = delegate;
    }

    @Nullable
    @Contract("null -> null")
    public static VFileAdapter createInstance(@Nullable VirtualFile delegate) {
        return delegate == null ? null : new VFileAdapter(delegate);
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
            children.add(createInstance(delegateChild));
        }
        return children;
    }
}
