import com.intellij.psi {
    PsiElement
}
import com.intellij.usages.impl.rules {
    UsageTypeProvider,
    UsageType
}
import com.intellij.psi.util {
    PsiTreeUtil
}
import ceylon.interop.java {
    javaClass
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonPsi
}

shared class CeylonUsageTypeProvider() satisfies UsageTypeProvider {
    
    shared actual UsageType? getUsageType(PsiElement el) { 
        if (exists invocation = PsiTreeUtil.getParentOfType(el, 
                javaClass<CeylonPsi.InvocationExpressionPsi>()),
            invocation.ceylonNode.primary.endIndex.intValue() >= el.textRange.endOffset,
            exists type = PsiTreeUtil.getParentOfType(el, 
                javaClass<CeylonPsi.BaseTypeExpressionPsi>())
                else PsiTreeUtil.getParentOfType(el, 
                javaClass<CeylonPsi.QualifiedTypeExpressionPsi>())) {
            
            return UsageType.\iCLASS_NEW_OPERATOR;
        }

        if (is CeylonPsi.ImportMemberPsi p = el.parent) {
            return UsageType.\iCLASS_IMPORT;
        }
        
        if (exists param = PsiTreeUtil.getParentOfType(el, 
            javaClass<CeylonPsi.TypeArgumentListPsi>())) {
            
            return UsageType.\iTYPE_PARAMETER;
        }
        
        if (exists param = PsiTreeUtil.getParentOfType(el, 
            javaClass<CeylonPsi.ValueParameterDeclarationPsi>())) {
            
            return UsageType.\iCLASS_METHOD_PARAMETER_DECLARATION;
        }
        
        return null;
    }
}
