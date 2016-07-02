import com.intellij.psi {
    ElementDescriptionLocation,
    ElementDescriptionProvider,
    PsiElement
}
import com.intellij.usageView {
    UsageViewLongNameLocation,
    UsageViewTypeLocation,
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
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.impl {
    ParameterPsiIdOwner
}

shared String kind(PsiElement element) {
    if (is CeylonPsi.AnyClassPsi element) {
        return "class";
    } else if (is CeylonPsi.AnyInterfacePsi element) {
        return "interface";
    } else if (is CeylonPsi.AttributeDeclarationPsi element) {
        return if (element.parent is CeylonPsi.ClassBodyPsi|CeylonPsi.InterfaceBodyPsi)
            then "attribute" else "value";
    } else if (is CeylonPsi.AnyMethodPsi element) {
        for (a in element.ceylonNode.annotationList.annotations) {
            if (a.primary.token.text=="annotation") {
                return "annotation";
            }
        }
        return if (element.parent is CeylonPsi.ClassBodyPsi|CeylonPsi.InterfaceBodyPsi)
            then "method" else "function";
    } else if (is CeylonPsi.AttributeArgumentPsi element) {
        return "value argument";
    } else if (is CeylonPsi.MethodArgumentPsi element) {
        return "function argument";
    } else if (is ParameterPsiIdOwner element) {
        return "function parameter";
    } else if (is CeylonPsi.ParameterPsi element) {
        return "parameter";
    } else if (is CeylonPsi.TypeParameterDeclarationPsi element) {
        return "type parameter";
    } else if (is CeylonPsi.ObjectDefinitionPsi element) {
        return "object";
    } else if (is CeylonPsi.ObjectArgumentPsi element) {
        return "object argument";
    } else if (is CeylonPsi.ConstructorPsi element) {
        return "constructor";
    } else if (is CeylonPsi.EnumeratedPsi element) {
        return "value constructor";
    } else if (is CeylonPsi.TypeAliasDeclarationPsi element) {
        return "alias";
    } else if (is CeylonPsi.PackageDescriptorPsi element) {
        return "package";
    } else if (is CeylonPsi.ModuleDescriptorPsi element) {
        return "module";
    } else if (is CeylonPsi.VariablePsi element) {
        return "variable";
    } else if (is CeylonPsi.AttributeSetterDefinitionPsi element) {
        return "setter";
    }
//    logger.warn("Can't find type name for class " + className(element));
    return "declaration";
}

shared class CeylonElementDescriptionProvider() satisfies ElementDescriptionProvider {
    
    shared actual String? getElementDescription(PsiElement element,
            ElementDescriptionLocation location) {
        if (is CeylonCompositeElement element) {
            return switch (location)
                case (is UsageViewTypeLocation) kind(element)
                case (is UsageViewLongNameLocation) descriptions.descriptionForPsi(element)
                case (is UsageViewShortNameLocation) element.name
                else element.name; //there are more cases to consider here!
        }
        else {
            return null;
        }
    }
    
}

shared object descriptions {

    shared String descriptionForDeclaration(Declaration decl,
            Boolean includeKeyword = true,
            Boolean includeContainer = true,
            Boolean includeReturnType = true,
            Unit unit = decl.unit) {
        value result = StringBuilder();
        if (includeKeyword) {
            result.append(keyword(decl)).append(" ");
        }
        if (includeContainer) {
            result.append(container(decl));
        }
        result.append(decl.name else "new")
                .append(parameterLists(decl, unit));
        if (includeReturnType,
            is TypedDeclaration decl,
            !ModelUtil.isConstructor(decl)) {
            if (is Function decl, decl.declaredVoid) {
                //noop for void
            }
            if (is Value decl, ModelUtil.isObject(decl)) {
                //noop for anon classes
            }
            else if (exists returnType = decl.type,
                    !ModelUtil.isTypeUnknown(returnType)) {
                result.append(" ∊ ")
                    .append(returnType.asString(unit));
            }
        }
        return result.string;
    }
    
    shared String descriptionForNode(Tree.Declaration|Tree.TypedArgument node, Boolean includeKeyword = true) {
        value result = StringBuilder();
        if (includeKeyword) {
            result.append(nodeKeyword(node)).append(" ");
        }
        Tree.Identifier? id
                = switch (node)
                case (is Tree.Declaration) node.identifier
                case (is Tree.TypedArgument) node.identifier;
        result.append(id?.text else "new");
        switch (node)
        case (is Tree.AnyClass) {
            if (exists pl = node.parameterList) {
                appendTreeParameters(result, pl);
            }
        }
        case (is Tree.AnyMethod) {
            for (pl in node.parameterLists) {
                appendTreeParameters(result, pl);
            }
        }
        case (is Tree.MethodArgument) {
            for (pl in node.parameterLists) {
                appendTreeParameters(result, pl);
            }
        }
        case (is Tree.Constructor) {
            if (exists pl = node.parameterList) {
                appendTreeParameters(result, pl);
            }
        }
        else {}
        return result.string;
    }

    shared String? descriptionForPsi(CeylonCompositeElement element,
        Boolean includeKeyword = true,
        Boolean includeContainer = true,
        Boolean includeReturnType = true) {
        value node = element.ceylonNode;
        value decl =
            switch (node)
            case (is Tree.Declaration) node.declarationModel
            case (is Tree.TypedArgument) node.declarationModel
            case (is Tree.SpecifierStatement) (node.refinement then node.declaration)
            else null;

        if (exists decl) {
            return descriptionForDeclaration {
                decl = decl;
                includeKeyword = includeKeyword;
                includeContainer = includeContainer;
                includeReturnType = includeReturnType;
                unit = node.unit else decl.unit;
            };
        }
        else if (is Tree.Declaration|Tree.TypedArgument node) {
            return descriptionForNode(node, includeKeyword);
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
            case (is Interface) "interface"
            case (is Class) (declaration.objectClass then "object" else "class")
            case (is Value) (ModelUtil.isObject(declaration) then "object" else "value")
            case (is Function) (declaration.declaredVoid then "void" else "function")
            case (is TypeAlias) "alias"
            case (is Constructor) "new"
            else "";
    }

    String nodeKeyword(Tree.Declaration|Tree.TypedArgument declaration)
            => switch (declaration)
            case (is Tree.ObjectDefinition|Tree.ObjectArgument) "object"
            case (is Tree.AnyClass) "class"
            case (is Tree.AnyInterface) "interface"
            case (is Tree.AnyAttribute|Tree.AttributeArgument) "value"
            case (is Tree.TypeAliasDeclaration) "alias"
            case (is Tree.Constructor|Tree.Enumerated) "new"
            case (is Tree.AnyMethod) (declaration.type is Tree.VoidModifier then "void" else "function")
            case (is Tree.MethodArgument) (declaration.type is Tree.VoidModifier then "void" else "function")
            else "";

    String parameterLists(Declaration decl, Unit unit) {
        if (!is Functional decl) {
            return "";
        }
        value builder = StringBuilder();
        for (paramList in decl.parameterLists) {
            appendParameters(builder, paramList, unit);
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
            if (exists model = param.model) {
                builder.append(parameterLists(model, unit));
            }
        }
        builder.append(")");
    }

    void appendTreeParameters(StringBuilder builder, Tree.ParameterList paramList) {
        builder.append("(");
        variable value first = true;
        for (param in paramList.parameters) {
            if (first) {
                first = false;
            }
            else {
                builder.append(", ");
            }
            switch (param)
            case (is Tree.InitializerParameter) {
                builder.append(param.identifier.text);
            }
            case (is Tree.ParameterDeclaration) {
                if (exists id = param.typedDeclaration.identifier) {
                    builder.append(id.text);
                }
                if (is Tree.AnyMethod dec = param.typedDeclaration) {
                    for (pl in dec.parameterLists) {
                        appendTreeParameters(builder, pl);
                    }
                }
            }
            //TODO: pattern parameters?
            else {}
        }
        builder.append(")");
    }
}