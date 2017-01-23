import com.intellij.psi {
    PsiElement
}
import com.intellij.usages.impl.rules {
    UsageTypeProvider,
    UsageType
}

import org.intellij.plugins.ceylon.ide.psi {
    CeylonPsi {
        ...
    }
}

shared class CeylonUsageTypeProvider() satisfies UsageTypeProvider {

    value functionInvocation = UsageType("Function invocation");
    value evaluationOrFunctionReference = UsageType("Evaluation or function reference");
    value valueOrFunctionTypeDeclaration = UsageType("Value or function declaration");
    value aliasedType = UsageType("Aliased type");
    value meta = UsageType("Metamodel usage");

    shared actual UsageType? getUsageType(PsiElement el) {
        if (el.parent is ImportMemberOrTypePsi) {
            return UsageType.classImport;
        }

        if (el.parent.parent is TypeLiteralPsi
          ||el.parent is MemberLiteralPsi) {
            return meta;
        }

        if (el.parent is SimpleTypePsi) {
            variable PsiElement type = el.parent;
            while (is TypePsi parent = type.parent) {
                type = parent;
            }
            if (type.parent.parent is ParameterDeclarationPsi) {
                return UsageType.classMethodParameterDeclaration;
            }
            else if (type.parent is TypedDeclarationPsi) {
                return valueOrFunctionTypeDeclaration;
            }
            else if (type.parent is ExtendedTypePsi|SatisfiedTypesPsi) {
                return UsageType.classExtendsImplementsList;
            }
            else if (type.parent is TypeArgumentListPsi) {
                return UsageType.typeParameter;
            }
            else if (type.parent is TypeSpecifierPsi|ClassSpecifierPsi) {
                return aliasedType;
            }
            else if (type.parent is IsConditionPsi|IsCasePsi|IsOpPsi) {
                return UsageType.classInstanceOf;
            }
        }

        if (el.parent is StaticMemberOrTypeExpressionPsi) {
            value ref = el.parent;
            if (ref.parent is AnnotationPsi) {
                return UsageType.annotation;
            }
            else if (ref.parent is InvocationExpressionPsi) {
                return ref is BaseTypeExpressionPsi
                            | QualifiedTypeExpressionPsi
                    then UsageType.classNewOperator
                    else functionInvocation;
            }
            else {
                return evaluationOrFunctionReference;
            }
        }

        return null;
    }
}
