import ceylon.interop.java {
    javaString,
    CeylonIterable
}
import com.github.rjeschke.txtmark {
    Processor
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Value,
    TypeDeclaration,
    TypedDeclaration,
    Declaration,
    Package,
    Module,
    Class,
    Interface,
    TypeAlias,
    Constructor,
    FunctionOrValue,
    UnknownType,
    Function,
    Functional,
    Parameter
}
import java.lang {
    JString=String
}
import ceylon.collection {
    ArrayList
}

shared JString? getDocumentation(Tree.CompilationUnit rootNode, Integer offset) {
    value node = getHoverNode(rootNode, offset);
    variable String? doc = null;
    
    if (exists node) {
        if (is Tree.LocalModifier node) {
            // TODO
        } else if (is Tree.Literal node) {
            // TODO
        } else {
            Referenceable? model = nodes.getReferencedDeclaration(node, rootNode);
            
            if (exists model) {
                doc = getDocumentationText(model, node);
            }
        }
    }
    
    if (exists str = doc) {
        return javaString(str);
    }
    
    return null;
}

void addPackage(Declaration decl, StringBuilder builder) {
    Package? pkg = (decl of Referenceable).unit.\ipackage;
    if (exists pkg, !pkg.qualifiedNameString.empty) {
        builder.append("<p><b>``pkg.qualifiedNameString``</b></p>\n");
    }
}

void addSignature(Declaration decl, StringBuilder builder) {
    if (decl.shared) { builder.append("shared "); }
    if (decl.actual) { builder.append("actual "); }
    if (decl.default) { builder.append("default "); }
    if (decl.formal) { builder.append("formal "); }
    if (is Value decl, decl.late) { builder.append("late "); }
    if (is TypedDeclaration decl, decl.variable) { builder.append("variable "); }
    // FIXME does not compile if (exists nat = decl.native) { builder.append("native "); }
    if (is TypeDeclaration decl) {
        if (decl.sealed) { builder.append("sealed "); }
        if (decl.final) { builder.append("final "); }
        if (is Class decl, decl.abstract) { builder.append("abstract "); }
    }
    if (decl.annotation) { builder.append("annotation "); }
    
    if (is Class decl) {
        if (decl.anonymous) {
            builder.append("object ");
        } else {
            builder.append("class ");
        }
    } else if (is Interface decl) {
        builder.append("interface ");
    } else if (is TypeAlias decl) {
        builder.append("alias ");
    } else if (is Constructor decl) {
        builder.append("new ");
    } else if (is TypedDeclaration decl) {
        addTypedDeclarationSignature(decl, builder);
    }
    
    builder.append("<b>`` decl.name else "" ``</b>");
    
    addTypeParameters(decl, builder);
    addParameters(decl, builder);
    addInheritance(decl, builder);
}

void addDoc(Declaration decl, StringBuilder builder) {
    value doc = CeylonIterable(decl.annotations).find((ann) => ann.name.equals("doc") || ann.name.empty);
    
    if (exists doc, !doc.positionalArguments.empty) {
        value string = markdown(doc.positionalArguments.get(0).string);
        builder.append("<div>\n").append(string).append("</div>");
    }
}

String markdown(String text) {
    //Builder builder = Configuration.builder().forceExtentedProfile();
    //builder.setSpecialLinkEmitter(UnlinkedSpanEmitter());
    
    return Processor.process(text);
}

void addInheritance(Declaration decl, StringBuilder builder) {
    if (is TypedDeclaration decl) {
    } else if (is TypeDeclaration decl) {
        if (exists cases = decl.type.caseTypes) {
            builder.append("\nof ").append(" | ".join(CeylonIterable(cases).map((c) => c.declaration.name)));
            
            // FIXME compilation error
            //if (exists it = decl.selfType) {
            //    builder.append(" (self type)");
            //}
        }
        
        if (is Class decl) {
            if (exists sup = decl.extendedType) {
                builder.append("\nextends ").append(sup.asString());
            }
        }
        
        if (!decl.satisfiedTypes.empty) {
            builder.append("\nsatisfies ").append(" &amp; ".join(CeylonIterable(decl.satisfiedTypes).map((s) => s.asString())));
        }
    }
}

void addTypedDeclarationSignature(TypedDeclaration decl, StringBuilder builder) {
    value sequenced = isSequenced(decl);
    
    value type = if (sequenced, exists t = decl.type, !t.typeArgumentList.empty) then decl.type else UnknownType((decl of Referenceable).unit).type;
    
    if (decl.dynamicallyTyped) {
        builder.append("dynamic ");
    } else if (is Value decl, type.declaration.anonymous, !type.typeConstructor) {
        builder.append("object ");
    } else if (is Function decl) {
        if (decl.declaredVoid) {
            builder.append("void ");
        } else {
            builder.append(type.asString());
        }
    }
    
    if (sequenced) {
        if (is FunctionOrValue decl, decl.initializerParameter.atLeastOne) {
            builder.append("+ ");
        } else {
            builder.append("* ");
        }
    }
}

Boolean isSequenced(TypedDeclaration decl) {
    if (is FunctionOrValue decl) {
        return decl.parameter && decl.initializerParameter.sequenced;
    }
    
    return false;
}

void addTypeParameters(Declaration decl, StringBuilder builder) {
}

void addParameters(Declaration decl, StringBuilder builder) {
    if (is Functional decl, exists plists = decl.parameterLists) {
        CeylonIterable(plists).each(void(element) {
                value params = { for (param in CeylonIterable(element.parameters)) addParameter(param, builder) };
                builder.append("(").append(", ".join(params)).append(")");
            });
    }
}

String addParameter(Parameter param, StringBuilder builder) {
    if (exists model = param.model) {
        return model.type.declaration.name + " " + param.name;
    } else {
        return param.name;
    }
}

void addParametersDoc(Declaration decl, StringBuilder builder) {
    if (is Functional decl, exists plists = decl.parameterLists, !plists.empty) {
        value list = ArrayList<Parameter>();
        
        CeylonIterable(plists).each(void(element) {
                for (param in CeylonIterable(element.parameters)) {
                    list.add(param);
                }
            });
        
        if (!list.empty) {
            builder.append("\n<dd><dl>\n<dt><b>Parameters:</b></dt>\n");
            list.each(void(param) {
                    builder.append("<dd><code>``param.name``</code> - ``getParamDoc(param)``</dd>\n");
                });
            builder.append("</dl></dd>");
        }
    }
}

String getParamDoc(Parameter param) {
    if (exists model = param.model) {
        value ann = CeylonIterable(model.annotations).find((ann) => ann.name.empty || ann.name.equals("doc"));
        
        if (exists ann, !ann.positionalArguments.empty) {
            return ann.positionalArguments.get(0).string;
        }
    }
    
    return "";
}

String getDeclarationDoc(Declaration model, Node node) {
    variable value decl = model;
    if (is Value model) {
        TypeDeclaration? typeDecl = model.typeDeclaration;
        
        if (exists typeDecl, typeDecl.anonymous, !model.type.typeConstructor) {
            decl = typeDecl;
        }
    }
    
    value builder = StringBuilder();
    
    addPackage(decl, builder);
    builder.append("<pre>");
    addSignature(decl, builder);
    builder.append("</pre>\n");
    addDoc(decl, builder);
    addParametersDoc(decl, builder);
    
    return builder.string;
}

String? getDocumentationText(Referenceable model, Node node) {
    if (is Declaration model) {
        return getDeclarationDoc(model, node);
    } else if (is Package model) {
        // TODO
    } else if (is Module model) {
        // TODO
    }
    
    return null;
}

shared Node? getHoverNode(Tree.CompilationUnit rootNode, Integer offset) {
    return nodes.findNode(rootNode, offset);
}
