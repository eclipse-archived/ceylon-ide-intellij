import com.intellij.codeInsight.navigation.actions {
    TypeDeclarationProvider
}
import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration,
    ModelUtil,
    TypeDeclaration,
    UnionType,
    IntersectionType
}

import java.lang {
    ObjectArray
}
import java.util {
    ArrayList
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonTypeDeclarationProvider() satisfies TypeDeclarationProvider {

    shared actual ObjectArray<PsiElement>? getSymbolTypeDeclarations(PsiElement psiElement) {
        TypeDeclaration typeDec;
        if (is CeylonPsi.StaticMemberOrTypeExpressionPsi psiElement,
            is TypedDeclaration dec = psiElement.ceylonNode.declaration,
            exists type = dec.type,
            !ModelUtil.isTypeUnknown(type)) {
            typeDec = type.declaration;
        }
        else if (is CeylonPsi.TypedDeclarationPsi psiElement,
            exists type = psiElement.ceylonNode.type?.typeModel,
            !ModelUtil.isTypeUnknown(type)) {
            typeDec = type.declaration;

        }
        else {
            return null;
        }
        value project = psiElement.project;
        if (is UnionType typeDec) {
            value list = ArrayList<PsiElement>();
            for (t in typeDec.caseTypes) {
                list.add(resolveDeclaration(t.declaration, project));
            }
            return list.toArray(ObjectArray<PsiElement>(0));
        }
        if (is IntersectionType typeDec) {
            value list = ArrayList<PsiElement>();
            for (t in typeDec.satisfiedTypes) {
                list.add(resolveDeclaration(t.declaration, project));
            }
            return list.toArray(ObjectArray<PsiElement>(0));
        }
        value array = ObjectArray<PsiElement>(1);
        array.set(0, resolveDeclaration(typeDec, project));
        return array;
    }

}