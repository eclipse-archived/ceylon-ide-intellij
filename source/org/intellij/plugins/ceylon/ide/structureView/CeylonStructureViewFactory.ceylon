import com.intellij.ide.structureView {
    StructureViewBuilder,
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

shared class CeylonStructureViewFactory() satisfies PsiStructureViewFactory {

    shared actual StructureViewBuilder? getStructureViewBuilder(PsiFile psiFile) {
        if (is CeylonFile psiFile) {
            return object extends TreeBasedStructureViewBuilder() {
                createStructureViewModel(Editor editor) => CeylonFileTreeModel(psiFile);
                rootNodeShown => false;
            };
        }
        return null;
    }
}
