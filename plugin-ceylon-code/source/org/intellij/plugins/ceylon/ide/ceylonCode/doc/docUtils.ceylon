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
    Parameter,
    Unit,
    Scope,
    Reference,
    Type,
    ClassOrInterface
}
import java.lang {
    JString=String,
    StringBuilder,
    JCharacter=Character {
        UnicodeScript,
        UnicodeBlock
    }    
}
import java.awt {
    Font
}
import ceylon.collection {
    ArrayList
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey,
    EditorColorsManager,
    CodeInsightColors
}
import com.intellij.openapi.editor {
    DefaultLanguageHighlighterColors
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
}

shared object docGenerator {

    String psiProtocol = "psi_element://";

    String escape(String content) => content.replace("&", "&amp;").replace("\"", "&quot;").replace("<", "&lt;").replace(">", "&gt;");

    String hexColor(Integer red, Integer green, Integer blue) {
        return "#" + formatInteger(red, 16).padLeading(2, '0') + formatInteger(green, 16).padLeading(2, '0') + formatInteger(blue, 16).padLeading(2, '0');
    }
    
    String color(Object? what, TextAttributesKey how) {
        value attributes = EditorColorsManager.instance.globalScheme.getAttributes(how);
        value color = "color:``hexColor(attributes.foregroundColor.red, attributes.foregroundColor.green, attributes.foregroundColor.blue)``";
        value bold = if (attributes.fontType.and(Font.\iBOLD) != 0) then "font-weight: bold" else "";
        value italic = if (attributes.fontType.and(Font.\iITALIC) != 0) then "font-size: italic" else "";
        
        return "<code style='``color``; ``bold``; ``italic``'>``what else "<error>"``</code>";
    }
    
    void appendCharacterInfo(StringBuilder builder, String string) {
        builder.append(color(escape("'``string``'"), DefaultLanguageHighlighterColors.\iSTRING)).append("<br/>\n");
        value codepoint = JCharacter.codePointAt(javaString(string), 0);
        builder.append("Unicode name: ").append(JCharacter.getName(codepoint)).append("<br/>\n");
        builder.append("Codepoint: <code>U+").append(formatInteger(codepoint, 16).uppercased.padLeading(4, '0')).append("</code><br/>\n");
        // TODO general category name
        builder.append("Script: <code>").append(UnicodeScript.\iof(codepoint).name()).append("</code><br/>\n");
        builder.append("Block: <code>").append(UnicodeBlock.\iof(codepoint).string).append("</code>");
    }
    
    String buildUrl(Referenceable model) {
        if (is Package model) {
            return buildUrl(model.\imodule) + ":" + model.nameAsString;
        }
        if (is Module model) {
            return model.nameAsString + "/" + model.version;
        }
        else if (is Declaration model) {
            String result = ":" + model.name;
            Scope? container = model.container;
            if (is Referenceable container) {
                return buildUrl(container) + result;
            }
            else {
                return result;
            }
        }
        else {
            return "";
        }
    }
    
    String buildLink(Referenceable model, String text) {
        return "<a href=\"``psiProtocol``doc:``buildUrl(model)``\">``text``</a>";
    }

    object printer extends TypePrinter(true, true, false, true, false) {
        
        shared actual String getSimpleDeclarationName(Declaration? declaration, Unit unit) {
            if (exists declaration) {
                return buildLink(declaration, super.getSimpleDeclarationName(declaration, unit));
            }
            
            return "&lt;unknown&gt;";
        }
        
        shared actual String amp() => "&amp;";
        shared actual String lt() => "&lt;";
        shared actual String gt() => "&gt";
    }

    //see getTermTypeHoverText(Node node, String selectedText, IDocument doc, IProject project)
    String? getTermTypeText(Tree.Term term) {
        if (exists model = term.typeModel) {
            value builder = StringBuilder(if (is Tree.Literal term) then "Literal of type" else "Expression of type");
            builder.append(" ").append(printer.print(model, term.unit)).append("<br/>\n<br/>\n");
            
            if (is Tree.StringLiteral term) {
                value text = if (term.text.size < 250) then escape(term.text) else escape(term.text.spanTo(250)) + "...";
                builder.append(color("\"``text``\"", DefaultLanguageHighlighterColors.\iSTRING));
                
                // TODO display info for selected char? 
            } else if (is Tree.CharLiteral term, term.text.size > 2) {
                appendCharacterInfo(builder, term.text.span(1, 1));
            } else if (is Tree.NaturalLiteral term) {
                value text = term.text.replace("_", "");
                
                if (exists first = text.first, first == '#') {
                    builder.append(color(parseInteger(text.spanFrom(1), 16), DefaultLanguageHighlighterColors.\iNUMBER));
                } else if (exists first = text.first, first == '$') {
                    builder.append(color(parseInteger(text.spanFrom(1), 2), DefaultLanguageHighlighterColors.\iNUMBER));
                } else {
                    builder.append(color(parseInteger(text), DefaultLanguageHighlighterColors.\iNUMBER));
                }
            } else if (is Tree.FloatLiteral term) {
                builder.append(color(parseFloat(term.text.replace("_", "")), DefaultLanguageHighlighterColors.\iNUMBER));
            }
            
            return builder.string;
        }
        
        return null;
    }
    
    // see getInferredTypeHoverText(Node node, IProject project)
    String? getInferredTypeText(Tree.LocalModifier node) {
        if (exists model = node.typeModel) {
            return "Inferred type: <code>``printer.print(model, node.unit)``</code>";
        }
        
        return null;
    }
    
    void addPackage(Declaration decl, StringBuilder builder) {
        Package? pkg = (decl of Referenceable).unit.\ipackage;
        if (exists pkg, !pkg.qualifiedNameString.empty) {
            builder.append("<p><b>``pkg.qualifiedNameString``</b></p>\n");
        }
    }
    
    void addSignature(Declaration decl, StringBuilder builder, Node node) {
        value annotations = StringBuilder();
        if (decl.shared) { annotations.append("shared "); }
        if (decl.actual) { annotations.append("actual "); }
        if (decl.default) { annotations.append("default "); }
        if (decl.formal) { annotations.append("formal "); }
        if (is Value decl, decl.late) { annotations.append("late "); }
        if (is TypedDeclaration decl, decl.variable) { annotations.append("variable "); }
        // FIXME does not compile if (exists nat = decl.native) { annotations.append("native "); }
        if (is TypeDeclaration decl) {
            if (decl.sealed) { annotations.append("sealed "); }
            if (decl.final) { annotations.append("final "); }
            if (is Class decl, decl.abstract) { annotations.append("abstract "); }
        }
        if (decl.annotation) { annotations.append("annotation "); }
       
        builder.append(color(annotations.string, CodeInsightColors.\iANNOTATION_NAME_ATTRIBUTES));
       
        if (is Class decl) {
            if (decl.anonymous) {
                builder.append(color("object ", DefaultLanguageHighlighterColors.\iKEYWORD));
            } else {
                builder.append(color("class ", DefaultLanguageHighlighterColors.\iKEYWORD));
            }
        } else if (is Interface decl) {
            builder.append(color("interface ", DefaultLanguageHighlighterColors.\iKEYWORD));
        } else if (is TypeAlias decl) {
            builder.append(color("alias ", DefaultLanguageHighlighterColors.\iKEYWORD));
        } else if (is Constructor decl) {
            builder.append(color("new ", DefaultLanguageHighlighterColors.\iKEYWORD));
        } else if (is TypedDeclaration decl) {
            addTypedDeclarationSignature(decl, builder);
        }
        
        builder.append("<b>`` decl.name else "" ``</b>");
        
        addTypeParameters(decl, builder);
        addParameters(decl, builder, node);
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
            value unit = (decl of Referenceable).unit;
            
            if (exists cases = decl.type.caseTypes) {
                builder.append("\n")
                        .append(color("of ", DefaultLanguageHighlighterColors.\iKEYWORD))
                        .append(" | ".join(CeylonIterable(cases).map((c) => printer.print(c, unit))));
                
                // FIXME compilation error
                //if (exists it = decl.selfType) {
                //    builder.append(" (self type)");
                //}
            }
            
            if (is Class decl) {
                if (exists sup = decl.extendedType) {
                    builder.append("\n")
                            .append(color("extends ", DefaultLanguageHighlighterColors.\iKEYWORD))
                            .append(printer.print(sup, unit));
                }
            }
            
            if (!decl.satisfiedTypes.empty) {
                builder.append("\n")
                        .append(color("satisfies ", DefaultLanguageHighlighterColors.\iKEYWORD))
                        .append(" &amp; ".join(CeylonIterable(decl.satisfiedTypes).map((s) => printer.print(s, unit))));
            }
        }
    }
    
    void addTypedDeclarationSignature(TypedDeclaration decl, StringBuilder builder) {
        value sequenced = isSequenced(decl);
        
        value unit = (decl of Referenceable).unit;
        value type = getType(decl, sequenced, unit);
       
        if (decl.dynamicallyTyped) {
            builder.append(color("dynamic", DefaultLanguageHighlighterColors.\iKEYWORD));
        } else if (is Value decl, type.declaration.anonymous, !type.typeConstructor) {
            builder.append(color("object", DefaultLanguageHighlighterColors.\iKEYWORD));
        } else if (is Function decl) {
            if (decl.declaredVoid) {
                builder.append(color("void", DefaultLanguageHighlighterColors.\iKEYWORD));
            } else {
                builder.append(printer.print(type, unit));
            }
        } else {
            builder.append(printer.print(type, unit));
        }
        
        if (sequenced) {
            if (is FunctionOrValue decl, decl.initializerParameter.atLeastOne) {
                builder.append("+");
            } else {
                builder.append("*");
            }
        }
        
        builder.append(" ");
    }
    
    Type getType(TypedDeclaration decl, Boolean sequenced, Unit unit) {
        variable Type? type = decl.type;

        if (sequenced, exists t = decl.type, !t.typeArgumentList.empty) {
            type = t.typeArgumentList.get(0);
        }

        return type else UnknownType(unit).type;
    }
    
    Boolean isSequenced(TypedDeclaration decl) {
        if (is FunctionOrValue decl) {
            return decl.parameter && decl.initializerParameter.sequenced;
        }
        
        return false;
    }
    
    void addTypeParameters(Declaration decl, StringBuilder builder) {
    }
    
    void addParameters(Declaration decl, StringBuilder builder, Node node) {
        if (is Functional decl, exists plists = decl.parameterLists) {
            Reference ref = appliedReference(decl, node);
            
            CeylonIterable(plists).each(void(element) {
                value params = { for (param in CeylonIterable(element.parameters)) addParameter(param, builder, ref) };
                builder.append("(").append(", ".join(params)).append(")");
            });
        }
    }
    
    String addParameter(Parameter param, StringBuilder builder, Reference ref) {
        if (exists model = param.model) {
            value type = ref.getTypedParameter(param).type;
            
            return printer.print(type, (model of Referenceable).unit) + " " + param.name;
        } else {
            return param.name;
        }
    }
    
    Reference appliedReference(Declaration decl, Node node) {
        if (is TypeDeclaration decl) {
            return decl.type;
        } else if (is Tree.MemberOrTypeExpression node) {
            return node.target;
        } else if (is Tree.Type node) {
            return node.typeModel;
        } else {
            variable Type? qt = null;
            
            if (decl.classOrInterfaceMember, is ClassOrInterface ci = decl.container) {
                qt = ci.type;
            }
            
            return decl.appliedReference(qt, null);
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
    
    // see getDocumentationFor(CeylonParseController controller, Declaration dec, Node node, Reference pr)
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
        addSignature(decl, builder, node);
        builder.append("</pre>\n");
        addDoc(decl, builder);
        addParametersDoc(decl, builder);
        // TODO addClassMembers
        // TODO addNothingTypeInfo
        // TODO addUnitInfo
        
        return builder.string;
    }
                
    shared String? getDocumentationText(Referenceable model, Node node) {
        if (is Declaration model) {
            return getDeclarationDoc(model, node);
        } else if (is Package model) {
            return getPackageDoc(model, node);
        } else if (is Module model) {
            return getModuleDoc(model, node);
        }
        
        return null;
    }
    
    shared String getPackageDoc(Package pack, Node node) {
        value builder = StringBuilder();
        
        if (pack.shared) {
            builder.append(color("shared ", CodeInsightColors.\iANNOTATION_NAME_ATTRIBUTES));
        }
        
        builder.append(color("package ", DefaultLanguageHighlighterColors.\iKEYWORD)).append(pack.nameAsString).append("<br/>\n");
        
        // TODO documentation
        
        value mod = pack.\imodule;
        if (mod.java) {
            builder.append("<p>This package is implemented in Java.</p>\n");
        }
        if (JDKUtils.isJDKModule(mod.nameAsString)) {
            builder.append("<p>This package forms part of the Java SDK.</p>\n");             
        }
        
        // TODO? members
        
        if (mod.nameAsString.empty || mod.nameAsString.equals("default")) {
            builder.append("in default module\n");             
        } else {
            builder.append("in module ``mod.nameAsString`` \"``mod.version``\"");
            // TODO link to module
        }
        
        return builder.string;
    }

    shared String? getModuleDoc(Module mod, Node node) {
        value builder = StringBuilder();

        builder.append(color("module ", DefaultLanguageHighlighterColors.\iKEYWORD))
                .append(mod.nameAsString)
                .append(color(" \"``mod.version``\"", DefaultLanguageHighlighterColors.\iSTRING))
                .append("\n");
        
        if (mod.java) {
            builder.append("<p>This module is implemented in Java.</p>");
        }
        if (mod.default) {
            builder.append("<p>The default module for packages which do not belong to explicit module.</p>");
        }
        if (JDKUtils.isJDKModule(mod.nameAsString)) {
            builder.append("<p>This module forms part of the Java SDK.</p>");            
        }

        // TODO module doc
        // TODO? members
        
        return builder.string;
    }

    shared Node? getHoverNode(Tree.CompilationUnit rootNode, Integer offset) {
        return nodes.findNode(rootNode, offset);
    }    
    
    // see getHoverText(CeylonEditor editor, IRegion hoverRegion)
    shared JString? getDocumentation(Tree.CompilationUnit rootNode, Integer offset) {
        value node = getHoverNode(rootNode, offset);
        variable String? doc = null;
        
        if (exists node) {
            if (is Tree.LocalModifier node) {
                doc = getInferredTypeText(node);
            } else if (is Tree.Literal node) {
                doc = getTermTypeText(node);
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
}

