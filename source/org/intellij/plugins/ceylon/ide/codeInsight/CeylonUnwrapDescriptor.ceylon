import com.intellij.codeInsight.unwrap {
    UnwrapDescriptorBase,
    AbstractUnwrapper {
        AbstractContext
    }
}
import com.intellij.psi {
    PsiElement,
    PsiWhiteSpace
}
import com.intellij.psi.util {
    PsiTreeUtil
}

import java.util {
    List
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}
import java.lang {
    ObjectArray
}

shared class CeylonUnwrapDescriptor() extends UnwrapDescriptorBase() {

    shared class Context() extends AbstractContext() {

        isWhiteSpace(PsiElement element) => element is PsiWhiteSpace;

        shared void extractStatements(PsiElement parent, PsiElement clause) {
            if (exists block = PsiTreeUtil.getChildOfType(clause, `CeylonPsi.BlockPsi`),
                block.children.size>0) {
                value statements = block.children.array;
                extract(statements.first, statements.last, parent);
                delete(parent);
            }
        }
    }

    object unwrapper extends AbstractUnwrapper<Context>("Unwrap block") {

        createContext() => Context();

        getDescription(PsiElement element)
                => if (is CeylonPsi.ControlClausePsi element)
                then "Unwrap '``element.ceylonNode.mainToken.text``' block"
                else super.getDescription(element);

        function controlStatementParent(PsiElement element) {
            assert (exists immediate = PsiTreeUtil.getParentOfType(element, `CeylonPsi.ControlStatementPsi`));
            variable value result = immediate;
            //jump up to the top of a chain of if/elses
            while (is CeylonPsi.BodyPsi body = result.parent,
                body.ceylonNode.text != "{}",
                exists parent = PsiTreeUtil.getParentOfType(result, `CeylonPsi.ControlStatementPsi`)) {
                result = parent;
            }
            return result;
        }

        shared actual PsiElement collectAffectedElements(PsiElement element, List<PsiElement> toExtract) {
            super.collectAffectedElements(element, toExtract);
            return controlStatementParent(element);
        }

        shared actual void doUnwrap(PsiElement element, Context context) {
            if (is CeylonPsi.ControlClausePsi element) {
                context.extractStatements(controlStatementParent(element), element);
            }
        }

        isApplicableTo(PsiElement element)
                => if (element is CeylonPsi.ControlClausePsi,
                       exists block = PsiTreeUtil.getChildOfType(element, `CeylonPsi.BlockPsi`))
                then block.children.size>0
                else false;
    }

    createUnwrappers() => ObjectArray.with { unwrapper };

}