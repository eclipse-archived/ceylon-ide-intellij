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
    ClassOrInterface,
    TypedDeclaration
}

shared class CeylonElementDescriptionProvider() satisfies ElementDescriptionProvider {
    
    shared actual String? getElementDescription(PsiElement element,
        ElementDescriptionLocation location) 
            => if (is UsageViewLongNameLocation|UsageViewShortNameLocation location,
                    is CeylonCompositeElement element)
            then ceylonDeclarationDescriptionProvider.getDescription(element) 
            else null;
}

shared object ceylonDeclarationDescriptionProvider {

    shared String? getDescription(CeylonCompositeElement element, 
        Boolean includeKeyword = true, Boolean includeContainer = true) {

        value decl =
            switch (node = element.ceylonNode)
            case (is Tree.Declaration) node.declarationModel
            case (is Tree.SpecifierStatement) (node.refinement then node.declaration)
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

            if (is TypedDeclaration decl,
                !ModelUtil.isConstructor(decl)) {
                if (is Function decl, decl.declaredVoid) {
                    //noop for void
                }
                else if (exists returnType = decl.type,
                        !ModelUtil.isTypeUnknown(returnType)) {
                    sb.append(" ∊ ")
                        .append(returnType.asString((decl of Declaration).unit));
                }
            }

            return sb.string;
        }
        else if (is Tree.Declaration node = element.ceylonNode) {
            value sb = StringBuilder();

            if (includeKeyword) {
                sb.append(nodeKeyword(node)).append(" ");
            }
            
            sb.append(node.identifier?.text else "new");
            
            if (node is Tree.AnyClass|Tree.AnyMethod|Tree.Constructor) {
                sb.append("()"); //TODO: parameter lists!
            }

            return sb.string;
        }
        else {
            return null;
        }
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

    String nodeKeyword(Tree.Declaration declaration) {
        return switch (declaration)
        case (is Tree.AnyClass) "class"
        case (is Tree.AnyInterface) "interface"
        case (is Tree.AnyAttribute) "value"
        case (is Tree.AnyMethod) "function"
        case (is Tree.TypeAliasDeclaration) "alias"
        case (is Tree.Constructor|Tree.Enumerated) "new"
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
                        .append(unit.getSequentialElementType(type).asString(unit))
                        .append(" ");
            }
            else {
                builder.append(param.type.asString(unit))
                        .append(" ");
            }
            if (exists name = param.name) {
                builder.append(name);
            }
        }
        builder.append(")");
    }
    
}