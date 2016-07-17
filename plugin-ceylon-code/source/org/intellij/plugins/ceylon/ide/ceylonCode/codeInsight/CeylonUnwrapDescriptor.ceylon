import ceylon.interop.java {
    createJavaObjectArray,
    javaClass
}

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

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonUnwrapDescriptor() extends UnwrapDescriptorBase() {
    value controlStatementClass = javaClass<CeylonPsi.ControlStatementPsi>();
    value blockClass = javaClass<CeylonPsi.BlockPsi>();
    value bodyClass = javaClass<CeylonPsi.BodyPsi>();

    shared class Context() extends AbstractContext() {

        isWhiteSpace(PsiElement element) => element is PsiWhiteSpace;

        shared void extractStatements(PsiElement parent, PsiElement clause) {
            if (exists block = PsiTreeUtil.getChildOfType(clause, blockClass),
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
            assert (exists parent = PsiTreeUtil.getParentOfType(element, controlStatementClass));
            return parent;
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
                       exists block = PsiTreeUtil.getChildOfType(element, blockClass),
                       exists body = PsiTreeUtil.getParentOfType(element, bodyClass))
                then block.children.size>0 && body.ceylonNode.text == "{}"
                else false;
    }

    createUnwrappers() => createJavaObjectArray { unwrapper };

}