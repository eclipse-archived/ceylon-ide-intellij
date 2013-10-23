package org.intellij.plugins.ceylon;

import com.intellij.ide.highlighter.ArchiveFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;

public class CeylonFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(CeylonFileType.INSTANCE, CeylonFileType.DEFAULT_EXTENSION);
        consumer.consume(ArchiveFileType.INSTANCE, "src;car");
    }
}
