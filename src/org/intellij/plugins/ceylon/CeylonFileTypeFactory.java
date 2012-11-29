package org.intellij.plugins.ceylon;

import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import com.intellij.openapi.fileTypes.FileTypes;
import org.jetbrains.annotations.NotNull;

public class CeylonFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        consumer.consume(CeylonFileType.INSTANCE, CeylonFileType.DEFAULT_EXTENSION);
        consumer.consume(FileTypes.ARCHIVE, "src");
        consumer.consume(FileTypes.ARCHIVE, "car");
    }
}
