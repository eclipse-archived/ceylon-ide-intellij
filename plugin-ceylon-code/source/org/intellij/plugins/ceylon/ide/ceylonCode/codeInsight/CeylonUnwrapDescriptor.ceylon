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
    value statementClass = javaClass<CeylonPsi.StatementOrArgumentPsi>();
    value blockClass = javaClass<CeylonPsi.BlockPsi>();

    shared class Context() extends AbstractContext() {

        isWhiteSpace(PsiElement element) => element is PsiWhiteSpace;

        shared void extractStatements(PsiElement parent, PsiElement clause) {
            if (exists block = PsiTreeUtil.getChildOfType(clause, blockClass),
                exists statements = PsiTreeUtil.getChildrenOfType(block, statementClass)?.array) {
                extract(statements.first, statements.last, parent);
                delete(parent);
            }
        }
    }

    object ifUnwrapper extends AbstractUnwrapper<Context>("Unwrap 'if' block") {

        createContext() => Context();

        shared actual PsiElement collectAffectedElements(PsiElement element, List<PsiElement> toExtract) {
            super.collectAffectedElements(element, toExtract);
            return element.parent;
        }

        shared actual void doUnwrap(PsiElement element, Context context) {
            if (is CeylonPsi.IfClausePsi element) {
                context.extractStatements(element.parent, element);
            }
        }

        isApplicableTo(PsiElement element) => element is CeylonPsi.IfClausePsi;

    }

    object elseUnwrapper extends AbstractUnwrapper<Context>("Unwrap 'else' block") {

        createContext() => Context();

        shared actual PsiElement collectAffectedElements(PsiElement element, List<PsiElement> toExtract) {
            super.collectAffectedElements(element, toExtract);
            return element.parent;
        }

        shared actual void doUnwrap(PsiElement element, Context context) {
            if (is CeylonPsi.ElseClausePsi element) {
                context.extractStatements(element.parent, element);
            }
        }

        isApplicableTo(PsiElement element) => element is CeylonPsi.ElseClausePsi;

    }

    object forUnwrapper extends AbstractUnwrapper<Context>("Unwrap 'for' block") {

        shared actual String getDescription(PsiElement? e) => super.getDescription(e);

        createContext() => Context();

        shared actual PsiElement collectAffectedElements(PsiElement element, List<PsiElement> toExtract) {
            super.collectAffectedElements(element, toExtract);
            return element.parent;
        }

        shared actual void doUnwrap(PsiElement element, Context context) {
            if (is CeylonPsi.ForClausePsi element) {
                context.extractStatements(element.parent, element);
            }
        }

        isApplicableTo(PsiElement element) => element is CeylonPsi.ForClausePsi;

    }

    createUnwrappers() => createJavaObjectArray { ifUnwrapper, elseUnwrapper, forUnwrapper };


}