import com.intellij.ide.highlighter {
    ArchiveFileType
}
import com.intellij.openapi.fileTypes {
    FileTypeConsumer,
    FileTypeFactory
}

import org.eclipse.ceylon.ide.intellij.lang {
    CeylonFileType
}

shared class CeylonFileTypeFactory() extends FileTypeFactory() {

    shared actual void createFileTypes(FileTypeConsumer consumer) {
        consumer.consume(CeylonFileType.instance, CeylonFileType.instance.defaultExtension);
        consumer.consume(ArchiveFileType.instance, "src;car");
    }

}
