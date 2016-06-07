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
    ModelUtil,
    Declaration,
    Type,
    ParameterList,
    Class,
    Functional,
    Unit,
    Interface,
    Value,
    TypeAlias,
    Constructor,
    ClassOrInterface
}

shared class CeylonElementDescriptionProvider() satisfies ElementDescriptionProvider {
    
    shared actual String? getElementDescription(PsiElement element,
        ElementDescriptionLocation location) 
            => if (is UsageViewLongNameLocation|UsageViewShortNameLocation location) 
            then ceylonDeclarationDescriptionProvider.getDescription(element) 
            else null;
}

shared object ceylonDeclarationDescriptionProvider {

    shared String? getDescription(PsiElement|Declaration element, Boolean includeKeyword = true,
        Boolean includeContainer = true) {
        
        value decl = switch(element)
            case (is Declaration) element
            else if (is CeylonCompositeElement element,
                     is Tree.Declaration node = element.ceylonNode,
                     exists decl = node.declarationModel)
            then decl
            else if (is CeylonCompositeElement element,
                     is Tree.SpecifierStatement node = element.ceylonNode,
                     exists decl = node.declaration)
            then decl
            else null;

        if (exists decl) {
            value sb = StringBuilder();

            if (includeKeyword) {
                sb.append(keyword(decl)).append(" ");
            }
            if (includeContainer) {
                sb.append(container(decl));
            }
            sb.append(decl.name else "new")
                .append(parameterLists(decl));

            return sb.string;
        }
        return null;
    }
    
    String container(Declaration decl)
            => if (is ClassOrInterface container = decl.container)
            then container.name + "." else "";
    
    String keyword(Declaration declaration) {
        if (ModelUtil.isConstructor(declaration)) {
            return "new";
        }
        return switch (declaration)
        case (is Class) "class"
        case (is Interface) "interface"
        case (is Value) "value"
        case (is Function) "function"
        case (is TypeAlias) "alias"
        case (is Constructor) "new"
        else "";
    }
    
    String parameterLists(Declaration decl) {
        if (!is Functional decl) {
            return "";
        }
        value builder = StringBuilder();
        for (paramList in decl.parameterLists) {
            appendParameters(builder, paramList, 
                (decl of Declaration).unit);
        }
        return builder.string;
    }
    
    void appendParameters(StringBuilder builder, ParameterList paramList, Unit unit) {
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
                //builder.append("unknown");
            }
            else if (param.sequenced) {
                builder.append("*")
                        .append(unit.getSequentialElementType(type).asString())
                        .append(" ");
            }
            else {
                builder.append(param.type.asString())
                        .append(" ");
            }
            if (exists name = param.name) {
                builder.append(name);
            }
        }
        builder.append(")");
    }
    
}