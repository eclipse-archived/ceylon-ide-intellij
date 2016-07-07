import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    ...
}

import java.lang {
    JString=String
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class IdentifierElementManipulator()
        extends AbstractElementManipulator<CeylonPsi.IdentifierPsi>() {
    shared actual CeylonPsi.IdentifierPsi? handleContentChange(CeylonPsi.IdentifierPsi element, TextRange range, String newContent) {
        PsiFile file = PsiFileFactory.getInstance(element.project).createFileFromText(CeylonLanguage.instance, JString(newContent));
        if (exists identifier = file.findElementAt(0)) {
            assert (is CeylonPsi.IdentifierPsi? idPsi
                    = element.replace(identifier.parent));
            return idPsi;
        }
        else {
            return null;
        }
    }
}
