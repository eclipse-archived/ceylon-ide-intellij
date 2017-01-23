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
    ArrayList
}

import org.intellij.plugins.ceylon.ide.lang {
    ceylonFileType
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonCompositeElement,
    CeylonPsi
}

abstract class AbstractSurrounder() satisfies Surrounder {

    isApplicable(ObjectArray<PsiElement> elements) => elements.size!=0;

    shared PsiFile createDummyFile(Project project, String content)
            => PsiFileFactory.getInstance(project)
                .createFileFromText("dummy.ceylon",
                                    ceylonFileType,
                                    JString(content));

    shared PsiElement surround(ObjectArray<PsiElement> elements,
                CeylonPsi.StatementOrArgumentPsi surroundingStatement,
                CeylonPsi.BodyPsi block,
                CeylonPsi.StatementOrArgumentPsi targetPosition) {
        assert (exists firstElement = elements[0]);
        value parent = firstElement.parent;
        for (element in elements) {
            block.addBefore(element, targetPosition);
        }
        targetPosition.delete();
        value result = parent.addBefore(surroundingStatement, firstElement);
        for (element in elements) {
            element.delete();
        }
        return CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(result);
    }

}

object withIfSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){if(true){throw;}}";

    templateDescription => "'if' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
            ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists ifStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
            `CeylonPsi.IfStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 17,
            `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 18,
            `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, ifStatement, block, throwStatement);

        assert (exists ic
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.IfClausePsi`, true));
        assert (exists cl
                = PsiTreeUtil.findChildOfType(ic,
                    `CeylonPsi.ConditionListPsi`, true));

        value loc = cl.textOffset + 1;
        value len = cl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withIfElseSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){if(true){throw;}else{}}";

    templateDescription => "'if'/'else' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists ifStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
            `CeylonPsi.IfStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 17,
            `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 18,
            `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, ifStatement, block, throwStatement);

        assert (exists ic
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.IfClausePsi`, true));
        assert (exists cl
                = PsiTreeUtil.findChildOfType(ic,
                    `CeylonPsi.ConditionListPsi`, true));

        value loc = cl.textOffset + 1;
        value len = cl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withTryCatchSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){try{throw;}catch(e){e.printStackTrace();}}";

    templateDescription => "'try'/'catch' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
            ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    `CeylonPsi.TryCatchStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 12,
                    `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 13,
                    `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists ct
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.CatchClausePsi`, true));
        assert (exists bl
                = PsiTreeUtil.findChildOfType(ct,
                    `CeylonPsi.BlockPsi`, true));
        assert (exists pst
                = PsiTreeUtil.findChildOfType(ct,
                    `CeylonPsi.ExpressionStatementPsi`, true));

        value loc = pst.textOffset;
        value len = pst.textLength - 1;
        return TextRange(loc, loc + len);
    }

}

object withTryFinallySurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){try{throw;}finally{}}";

    templateDescription => "'try'/'finally' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
            ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    `CeylonPsi.TryCatchStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 12,
                    `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 13,
                    `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists fin
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.FinallyClausePsi`, true));
        assert (exists bl
                = PsiTreeUtil.findChildOfType(fin,
                    `CeylonPsi.BlockPsi`, true));

        value loc = bl.textOffset + 1;
        return TextRange(loc, loc);
    }

}

object withTryResourcesSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){try(){throw;}}";

    templateDescription => "'try' statement with resource list";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    `CeylonPsi.TryCatchStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 14,
                    `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 15,
                    `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists tc
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.TryClausePsi`, true));
        assert (exists rl
                = PsiTreeUtil.findChildOfType(tc,
                    `CeylonPsi.ResourceListPsi`, true));

        value loc = rl.textOffset + 1;
        value len = rl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withWhileSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){while(true){throw;break;}}";

    templateDescription => "'while' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists tryStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    `CeylonPsi.WhileStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 20,
                    `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 21,
                    `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, tryStatement, block, throwStatement);

        assert (exists wc
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.WhileClausePsi`, true));
        assert (exists cl
                = PsiTreeUtil.findChildOfType(wc,
                    `CeylonPsi.ConditionListPsi`, true));

        value loc = cl.textOffset + 1;
        value len = cl.textLength - 2;
        return TextRange(loc, loc + len);
    }

}

object withForSurrounder extends AbstractSurrounder() satisfies Surrounder {

    String content = "void a(){for(i in 0..0){throw;}}";

    templateDescription => "'for' statement";

    shared actual TextRange surroundElements(Project project, Editor editor,
    ObjectArray<PsiElement> elements) {

        value file = createDummyFile(project, content);

        assert (exists forStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    `CeylonPsi.ForStatementPsi`, true));
        assert (exists block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 23,
                    `CeylonPsi.BlockPsi`, true));
        assert (exists throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 24,
                    `CeylonPsi.ThrowPsi`, true));

        value formatted = surround(elements, forStatement, block, throwStatement);

        assert (exists fr
                = PsiTreeUtil.findChildOfType(formatted,
                    `CeylonPsi.ForClausePsi`, true));
        assert (exists it
                = PsiTreeUtil.findChildOfType(fr,
                    `CeylonPsi.ForIteratorPsi`, true));

        value loc = it.textOffset + 1;
        value len = it.textLength - 2;
        return TextRange(loc, loc + len);
    }

}


shared class CeylonSurroundDescriptor() satisfies SurroundDescriptor {

    object condition satisfies Condition<PsiElement> {
        \ivalue(PsiElement element)
                => element is CeylonPsi.StatementPsi
                && !element is CeylonPsi.VariablePsi
                             | CeylonPsi.TypeParameterDeclarationPsi;
    }

    shared actual ObjectArray<PsiElement> getElementsToSurround(PsiFile file,
            Integer selectionStart, Integer selectionEnd) {

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
                        `CeylonCompositeElement`)
                else startElem;
        value end
                = if (is PsiWhiteSpace endElem)
                then PsiTreeUtil.getPrevSiblingOfType(endElem,
                        `CeylonCompositeElement`)
                else endElem;

        value first = PsiTreeUtil.findFirstParent(start, condition);
        value last = PsiTreeUtil.findFirstParent(end, condition);
        if (!is CeylonPsi.StatementPsi first) {
            return PsiElement.emptyArray;
        }
        if (!is CeylonPsi.StatementPsi last) {
            return PsiElement.emptyArray;
        }

        value list = ArrayList<PsiElement>();
        variable CeylonPsi.StatementPsi? current = first;
        while (exists statement = current, statement!=last) {
            list.add(statement);
            current = PsiTreeUtil.getNextSiblingOfType(current,
                        `CeylonPsi.StatementPsi`);
        }
        list.add(last);
        return list.toArray(PsiElement.emptyArray);
    }

    surrounders => ObjectArray.with {
        withIfSurrounder,
        withIfElseSurrounder,
        withTryCatchSurrounder,
        withTryFinallySurrounder,
        withTryResourcesSurrounder,
        withForSurrounder,
        withWhileSurrounder
    };

    exclusive => false;
}
