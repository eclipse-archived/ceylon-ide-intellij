import ceylon.collection {
    ArrayList
}

import com.intellij.codeInsight.navigation.actions {
    TypeDeclarationProvider
}
import com.intellij.psi {
    PsiElement
}
import com.redhat.ceylon.model.typechecker.model {
    TypedDeclaration,
    Type,
    TypeDeclaration
}

import java.lang {
    ObjectArray
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi
}

shared class CeylonTypeDeclarationProvider() satisfies TypeDeclarationProvider {

    void addDeclarations(Type type, ArrayList<TypeDeclaration> list) {
        if (!type.unknown && !type.nothing) {
            if (type.union) {
                for (t in type.caseTypes) {
                    addDeclarations(t, list);
                }
            }
            else if (type.intersection) {
                for (t in type.satisfiedTypes) {
                    addDeclarations(t, list);
                }
            }
            else {
                value declaration = type.declaration;
                if (!declaration in list) {
                    list.add(declaration);
                }
                if (declaration.unit.isTupleType(type)) {
                    for (elementType in declaration.unit.getTupleElementTypes(type)) {
                        addDeclarations(elementType, list);
                    }
                }
                else if (declaration.unit.isIterableType(type)) {
                    if (!type.isSubtypeOf(declaration.unit.stringType),
                        exists iteratedType = declaration.unit.getIteratedType(type)) {
                        addDeclarations(iteratedType, list);
                    }
                }
                else if (declaration.unit.isCallableType(type)) {
                    if (exists iteratedType = declaration.unit.getCallableReturnType(type)) {
                        addDeclarations(iteratedType, list);
                    }
                    for (argType in declaration.unit.getCallableArgumentTypes(type)) {
                        addDeclarations(argType, list);
                    }
                }
            }
        }
    }

    shared actual ObjectArray<PsiElement>? getSymbolTypeDeclarations(PsiElement psiElement) {
        value list = ArrayList<TypeDeclaration>();
        switch (psiElement)
        case (is CeylonPsi.StaticMemberOrTypeExpressionPsi) {
            if (is TypedDeclaration dec = psiElement.ceylonNode?.declaration,
                exists type = dec.type) {
                addDeclarations(type, list);
            }
        }
        else case (is CeylonPsi.ConstructorPsi) {
            if (exists dec = psiElement.ceylonNode?.constructor,
                exists type = dec.extendedType) {
                list.add(type.declaration);
            }
        }
        else case (is CeylonPsi.TypedDeclarationPsi) {
            if (exists type = psiElement.ceylonNode?.type?.typeModel) {
                addDeclarations(type, list);
            }
        }
        else case (is CeylonPsi.TypedArgumentPsi) {
            if (exists type = psiElement.ceylonNode?.type?.typeModel) {
                addDeclarations(type, list);
            }
        }
        else case (is CeylonPsi.SpecifiedArgumentPsi) {
            if (exists type = psiElement.ceylonNode?.parameter?.type) {
                addDeclarations(type, list);
            }
        }
        else {}

        if (list.empty) {
            return null;
        }
        else {
            return ObjectArray.with {
                for (dec in list)
                resolveDeclaration(dec, psiElement.project)
            };
        }
    }

}