import com.intellij.ide.structureView {
    TreeBasedStructureViewBuilder
}
import com.intellij.lang {
    PsiStructureViewFactory
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.psi {
    PsiFile
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class CeylonStructureViewFactory()
        satisfies PsiStructureViewFactory {

    getStructureViewBuilder(PsiFile psiFile)
            => if (is CeylonFile psiFile)
            then object extends TreeBasedStructureViewBuilder() {
                createStructureViewModel(Editor editor)
                        => CeylonFileTreeModel(psiFile);
                rootNodeShown => false;
            }
            else null;
}
