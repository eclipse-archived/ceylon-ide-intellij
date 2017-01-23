import com.intellij.codeInsight.editorActions.moveUpDown {
    LineRange,
    StatementUpDownMover
}
import com.intellij.openapi.editor {
    Editor
}
import com.intellij.openapi.util {
    Condition
}
import com.intellij.psi {
    PsiElement,
    PsiFile
}
import com.intellij.psi.util {
    PsiTreeUtil {
        ...
    }
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

shared class CeylonStatementUpDownMover() extends StatementUpDownMover() {

    value statementClass = `CeylonPsi.StatementOrArgumentPsi`;
    value blockClass = `CeylonPsi.BlockPsi`;

    object condition satisfies Condition<PsiElement> {
        \ivalue(PsiElement element)
                => element is CeylonPsi.StatementOrArgumentPsi
                && !element is CeylonPsi.VariablePsi
                             | CeylonPsi.TypeParameterDeclarationPsi
                             | CeylonPsi.ForIteratorPsi;
    }

    shared actual Boolean checkAvailable(Editor editor, PsiFile file, MoveInfo moveInfo, Boolean down) {

        value pair = getElementRange(editor, file, getLineRangeFromSelection(editor));
        if (!exists pair) {
            return false;
        }

        value first = findFirstParent(pair.first, condition);
        value last = findFirstParent(pair.second, condition);
        if (!is CeylonPsi.StatementOrArgumentPsi first) {
            return false;
        }
        if (!is CeylonPsi.StatementOrArgumentPsi last) {
            return false;
        }

        if (exists other = if (down)
                then getNextSiblingOfType(last, statementClass)
                else getPrevSiblingOfType(first, statementClass)) {
            moveInfo.toMove = LineRange(first, last);
            moveInfo.toMove2 = LineRange(other);
            if (other is CeylonPsi.ControlStatementPsi) {
                value blocks = { *findChildrenOfType(other, blockClass) };
                if (exists block = if (down) then blocks.first else blocks.last,
                    exists brace = if (down) then block.firstChild else block.lastChild) {
                    moveInfo.toMove2 = LineRange(brace);
                }
            }
            return true;
        }
        else {
            return false;
        }
    }
}
