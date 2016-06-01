import com.intellij.psi {
    ElementDescriptionLocation,
    ElementDescriptionProvider,
    PsiElement
}
import com.intellij.usageView {
    UsageViewLongNameLocation,
    UsageViewShortNameLocation
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.model.typechecker.model {
    Function,
    ClassOrInterface,
    ModelUtil,
    Declaration,
    Type
}

shared class CeylonElementDescriptionProvider() satisfies ElementDescriptionProvider {

    shared actual String? getElementDescription(PsiElement element,
        ElementDescriptionLocation location) {

        if (is UsageViewLongNameLocation|UsageViewShortNameLocation location) {
            return ceylonDeclarationDescriptionProvider.getDescription(element);
        }

        return null;
    }
}

shared object ceylonDeclarationDescriptionProvider {

    shared String? getDescription(PsiElement|Declaration element) {
        value decl = switch(element)
        case (is Declaration) element
        else if (is CeylonCompositeElement element,
            is Tree.Declaration node = element.ceylonNode,
            exists decl = node.declarationModel)
        then decl
        else null;

        return switch (decl)
        case (is Null) null
        case (is ClassOrInterface)
            (decl.toplevel
                then decl.qualifiedNameString
                else decl.name)
        case (is Function)
                getFunctionSignature(decl)
        else decl.name;
    }
    
    String getFunctionSignature(Function fun) {
        value builder = StringBuilder();
        builder.append(fun.toplevel 
            then fun.qualifiedNameString 
            else fun.name);
        
        for (paramList in fun.parameterLists) {
            builder.append("(");
            variable value first = true;
            for (param in paramList.parameters) {
                if (first) {
                    first = false;
                }
                else {
                    builder.append(", ");
                }
                Type? type = param.type;
                if (ModelUtil.isTypeUnknown(type)) {
                    builder.append("unknown");
                }
                else if (param.sequenced) {
                    builder.append("*")
                           .append(fun.unit.getSequentialElementType(type).asString());
                }
                else {
                    builder.append(param.type.asString());
                }
            }
            builder.append(")");
        }
        
        return builder.string;
    }    
}