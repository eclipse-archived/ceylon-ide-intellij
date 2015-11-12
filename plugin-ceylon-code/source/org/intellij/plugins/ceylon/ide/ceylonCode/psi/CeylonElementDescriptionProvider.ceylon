import ceylon.interop.java {
    CeylonIterable
}

import com.intellij.psi {
    ElementDescriptionLocation,
    ElementDescriptionProvider,
    PsiElement
}
import com.intellij.usageView {
    UsageViewLongNameLocation
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Function,
    ClassOrInterface
}

shared class CeylonElementDescriptionProvider() satisfies ElementDescriptionProvider {

    shared actual String? getElementDescription(PsiElement element,
        ElementDescriptionLocation location) {
        
        return findUsageDescriptionProvider.getElementDescription(element, location);
    }
}

object findUsageDescriptionProvider satisfies ElementDescriptionProvider {
    
    shared actual String? getElementDescription(PsiElement element, 
        ElementDescriptionLocation location) {
        
        if (is UsageViewLongNameLocation location,
            is CeylonCompositeElement element,
            is Tree.Declaration node = element.ceylonNode,
            exists decl = node.declarationModel) {
            
            return switch (decl)
            case (is ClassOrInterface) decl.qualifiedNameString
            case (is Function) getFunctionSignature(decl)
            else decl.nameAsString;
        }
        
        return null;
    }
    
    String getFunctionSignature(Function fun) {
        value builder = StringBuilder()
                .append(fun.toplevel then fun.qualifiedNameString else fun.nameAsString);
        
        for (paramList in CeylonIterable(fun.parameterLists)) {
            builder.append("(");
            value params = CeylonIterable(paramList.parameters);
            builder.append(", ".join(params.map((_) => _.type.asString())));
            builder.append(")");
        }
        
        return builder.string;
    }    
}