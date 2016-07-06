package org.intellij.plugins.ceylon.ide.ceylonCode.lang;

import com.intellij.ide.highlighter.ArchiveFileType;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.ceylonFileType_;
import org.jetbrains.annotations.NotNull;

public class CeylonFileTypeFactory extends FileTypeFactory {

    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        CeylonFileType type = CeylonFileType.INSTANCE;
        consumer.consume(type, type.getDefaultExtension());
        consumer.consume(ArchiveFileType.INSTANCE, "src;car");
    }
}
