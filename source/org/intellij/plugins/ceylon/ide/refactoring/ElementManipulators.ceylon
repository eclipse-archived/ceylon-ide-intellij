import ceylon.interop.java {
    javaString,
    javaClass
}

import com.intellij.openapi.util {
    TextRange
}
import com.intellij.psi {
    AbstractElementManipulator,
    PsiFileFactory
}

import org.intellij.plugins.ceylon.ide.lang {
    CeylonLanguage
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import com.intellij.psi.util {
    PsiTreeUtil
}

shared class IdentifierElementManipulator()
        extends AbstractElementManipulator<CeylonPsi.IdentifierPsi>() {
    shared actual CeylonPsi.IdentifierPsi? handleContentChange(
            CeylonPsi.IdentifierPsi element,
            TextRange range, String newContent) {
        value file
                = PsiFileFactory.getInstance(element.project)
                    .createFileFromText(CeylonLanguage.instance,
                        javaString(newContent));
        if (exists id = file.findElementAt(0)) {
            assert (is CeylonPsi.IdentifierPsi? psi
                    = element.replace(id.parent));
            return psi;
        }
        else {
            return null;
        }
    }
}

shared class StringLiteralElementManipulator()
        extends AbstractElementManipulator<CeylonPsi.StringLiteralPsi>() {
    shared actual CeylonPsi.StringLiteralPsi? handleContentChange(
            CeylonPsi.StringLiteralPsi element,
            TextRange range, String newContent) {
        value file
                = PsiFileFactory.getInstance(element.project)
                    .createFileFromText(CeylonLanguage.instance,
                        javaString(range.replace(element.text, newContent)));
        if (exists str = file.findElementAt(0)) {
            assert (is CeylonPsi.StringLiteralPsi? psi
                    = element.replace(str.parent));
            return psi;
        }
        else {
            return null;
        }
    }
}

shared class ImportPathElementManipulator()
        extends AbstractElementManipulator<CeylonPsi.ImportPathPsi>() {

    shared actual CeylonPsi.ImportPathPsi? handleContentChange(CeylonPsi.ImportPathPsi element,
            TextRange range, String newContent) {

        value file = PsiFileFactory.getInstance(element.project)
            .createFileFromText(CeylonLanguage.instance, javaString("import ``newContent`` {}"));

        if (exists path = PsiTreeUtil.findChildOfType(file, javaClass<CeylonPsi.ImportPathPsi>()),
            exists psi = element.replace(path),
            is CeylonPsi.ImportPathPsi psi) {
            return psi;
        }
        else {
            return null;
        }
    }
}
