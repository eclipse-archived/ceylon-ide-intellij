import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.vfs {
    VirtualFile
}
import com.redhat.ceylon.ide.common.model.parsing {
    RootFolderScanner
}

import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaCeylonProject
}
import org.intellij.plugins.ceylon.ide.ceylonCode.vfs {
    IdeaVirtualFolder
}

shared class IdeaRootFolderScanner(IdeaCeylonProject project, VirtualFile srcDir)
        extends RootFolderScanner<Module,VirtualFile,VirtualFile,VirtualFile>(
    project, IdeaVirtualFolder(srcDir, project), DummyProgressMonitor()
) {
}
