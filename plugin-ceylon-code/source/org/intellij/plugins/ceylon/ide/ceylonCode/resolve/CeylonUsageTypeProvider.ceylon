import ceylon.interop.java {
    javaClass
}

import com.intellij.psi {
    PsiElement
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import com.intellij.usages.impl.rules {
    UsageTypeProvider,
    UsageType
}

import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi {
        ...
    }
}

shared class CeylonUsageTypeProvider() satisfies UsageTypeProvider {

    shared actual UsageType? getUsageType(PsiElement el) {
        if (exists invocation = PsiTreeUtil.getParentOfType(el, javaClass<InvocationExpressionPsi>()),
            invocation.ceylonNode.primary.endIndex.intValue() >= el.textRange.endOffset,
            PsiTreeUtil.getParentOfType(el, javaClass<BaseTypeExpressionPsi>()) exists
         || PsiTreeUtil.getParentOfType(el, javaClass<QualifiedTypeExpressionPsi>()) exists,
            !PsiTreeUtil.getParentOfType(el, javaClass<QualifiedMemberExpressionPsi>()) exists
         /*&& !PsiTreeUtil.getParentOfType(el, javaClass<BaseMemberExpressionPsi>()) exists*/) {
            return UsageType.classNewOperator;
        }
        
        if (el.parent is ImportMemberPsi) {
            return UsageType.classImport;
        }
        
        if (PsiTreeUtil.getParentOfType(el, javaClass<TypeArgumentListPsi>()) exists) {
            return UsageType.typeParameter;
        }
        
        if (PsiTreeUtil.getParentOfType(el, javaClass<ValueParameterDeclarationPsi>()) exists) {
            return UsageType.classMethodParameterDeclaration;
        }
        
        if (PsiTreeUtil.getParentOfType(el, javaClass<ExtendedTypePsi>()) exists
         || PsiTreeUtil.getParentOfType(el, javaClass<SatisfiedTypesPsi>()) exists) {
            return UsageType.classExtendsImplementsList;
        }

        if (PsiTreeUtil.getParentOfType(el, javaClass<AnnotationPsi>()) exists) {
            return UsageType.annotation;
        }

        return null;
    }
}
