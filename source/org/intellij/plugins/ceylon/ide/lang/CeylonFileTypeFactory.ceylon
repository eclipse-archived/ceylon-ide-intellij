import com.intellij.ide.highlighter {
    ArchiveFileType
}
import com.intellij.openapi.fileTypes {
    FileTypeConsumer,
    FileTypeFactory
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonFileType
}

shared class CeylonFileTypeFactory() extends FileTypeFactory() {

    shared actual void createFileTypes(FileTypeConsumer consumer) {
        consumer.consume(CeylonFileType.instance, CeylonFileType.instance.defaultExtension);
        consumer.consume(ArchiveFileType.instance, "src;car");
    }

}
