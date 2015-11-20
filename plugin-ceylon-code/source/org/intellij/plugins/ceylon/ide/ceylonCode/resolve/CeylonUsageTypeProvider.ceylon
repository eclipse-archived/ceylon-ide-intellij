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
        if (exists invocation = PsiTreeUtil.getParentOfType(el,
                javaClass<InvocationExpressionPsi>()),
            invocation.ceylonNode.primary.endIndex.intValue() >= el.textRange.endOffset,
            exists type = PsiTreeUtil.getParentOfType(el,
                javaClass<BaseTypeExpressionPsi>())
                    else PsiTreeUtil.getParentOfType(el,
                javaClass<QualifiedTypeExpressionPsi>())) {
            
            return UsageType.\iCLASS_NEW_OPERATOR;
        }
        
        if (is ImportMemberPsi p = el.parent) {
            return UsageType.\iCLASS_IMPORT;
        }
        
        if (exists param = PsiTreeUtil.getParentOfType(el,
                javaClass<TypeArgumentListPsi>())) {
            
            return UsageType.\iTYPE_PARAMETER;
        }
        
        if (exists param = PsiTreeUtil.getParentOfType(el,
                javaClass<ValueParameterDeclarationPsi>())) {
            
            return UsageType.\iCLASS_METHOD_PARAMETER_DECLARATION;
        }
        
        if (PsiTreeUtil.getParentOfType(el, javaClass<ExtendedTypePsi>()) exists
            || PsiTreeUtil.getParentOfType(el, javaClass<SatisfiedTypesPsi>()) exists) {
            
            return UsageType.\iCLASS_EXTENDS_IMPLEMENTS_LIST;
        }
        
        return null;
    }
}
