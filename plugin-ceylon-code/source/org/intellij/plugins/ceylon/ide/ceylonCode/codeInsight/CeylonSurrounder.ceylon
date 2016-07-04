import ceylon.interop.java {
    javaClass,
    createJavaObjectArray
}

import com.intellij.codeInsight {
    CodeInsightUtilCore
}
import com.intellij.lang.surroundWith {
    SurroundDescriptor,
    Surrounder
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Condition,
    TextRange
}
import com.intellij.psi {
    PsiElement,
    PsiFile,
    PsiFileFactory,
    PsiWhiteSpace
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import java.lang {
    ObjectArray,
    JString=String
}
import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.ceylonCode.lang {
    CeylonFileType
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonCompositeElement,
    CeylonPsi
}

shared class CeylonWithIfSurrounder()
        satisfies Surrounder & SurroundDescriptor {

    templateDescription => "'if' statement";

    isApplicable(ObjectArray<PsiElement> elements) => true;

    shared actual TextRange surroundElements(Project project, Editor editor, ObjectArray<PsiElement> elements) {
        String content = "void a(){if(true){throw;}}";

        value file
                = PsiFileFactory.getInstance(project)
                    .createFileFromText("dummy.ceylon",
                                        CeylonFileType.instance,
                                        JString(content));

        assert (exists ifStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    javaClass<CeylonPsi.IfStatementPsi>(), true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 17,
                    javaClass<CeylonPsi.BlockPsi>(), true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 18,
                    javaClass<CeylonPsi.ThrowPsi>(), true));

        assert (exists firstElement = elements[0]);
        value parent = firstElement.parent;

        for (element in elements) {
            block.addBefore(element, throwStatement);
        }
        throwStatement.delete();

        value result = parent.addBefore(ifStatement, firstElement);
        for (element in elements) {
            element.delete();
        }

        value formatted = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(result);
        value loc = formatted.textOffset + formatted.text.indexOf("true");
        return TextRange(loc, loc + 4);
    }

   value condition = object satisfies Condition<PsiElement> {
        \ivalue(PsiElement element) => element is CeylonPsi.StatementOrArgumentPsi;
    };

    shared actual ObjectArray<PsiElement> getElementsToSurround(PsiFile file, Integer selectionStart, Integer selectionEnd) {

        value startElem = file.findElementAt(selectionStart);
        value endElem = file.findElementAt(selectionEnd);
        if (!exists startElem) {
            return PsiElement.emptyArray;
        }
        if (!exists endElem) {
            return PsiElement.emptyArray;
        }

        value start
                = if (is PsiWhiteSpace startElem)
                then PsiTreeUtil.getNextSiblingOfType(startElem,
                        javaClass<CeylonCompositeElement>())
                else startElem;
        value end
                = if (is PsiWhiteSpace endElem)
                then PsiTreeUtil.getPrevSiblingOfType(endElem,
                        javaClass<CeylonCompositeElement>())
                else endElem;

        value first = PsiTreeUtil.findFirstParent(start, condition);
        value last = PsiTreeUtil.findFirstParent(end, condition);
        if (!is CeylonPsi.StatementOrArgumentPsi first) {
            return PsiElement.emptyArray;
        }
        if (!is CeylonPsi.StatementOrArgumentPsi last) {
            return PsiElement.emptyArray;
        }

        List<PsiElement> list = ArrayList<PsiElement>();
        variable CeylonPsi.StatementOrArgumentPsi? current = first;
        while (exists statement = current, statement!=last) {
            list.add(statement);
            current = PsiTreeUtil.getNextSiblingOfType(current,
                        javaClass<CeylonPsi.StatementOrArgumentPsi>());
        }
        list.add(last);
        return list.toArray(PsiElement.emptyArray);
    }

    surrounders => createJavaObjectArray<Surrounder>({ this });

    exclusive => false;
}
