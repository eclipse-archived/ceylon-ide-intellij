import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaString,
    CeylonIterable
}

import com.github.rjeschke.txtmark {
    Processor,
    Configuration
}
import com.intellij.icons {
    AllIcons
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey,
    EditorColorsManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.util {
    PlatformIcons
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.util {
    nodes
}
import com.redhat.ceylon.model.cmr {
    JDKUtils
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
    ClassOrInterface,
    Annotated
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}

import java.awt {
    Font
}
import java.lang {
    JString=String,
    StringBuilder,
    JCharacter=Character {
        UnicodeScript,
        UnicodeBlock
    }
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    ceylonHighlightingColors
}
import com.intellij.openapi.util {
    IconLoader
}

String psiProtocol = "psi_element://";

shared object docGenerator {

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
        builder.append(color(escape("'``string``'"), ceylonHighlightingColors.strings)).append("<br/>\n");
        value codepoint = JCharacter.codePointAt(javaString(string), 0);
        builder.append("Unicode name: ").append(JCharacter.getName(codepoint)).append("<br/>\n");
        builder.append("Codepoint: <code>U+").append(formatInteger(codepoint, 16).uppercased.padLeading(4, '0')).append("</code><br/>\n");
        // TODO general category name
        builder.append("Script: <code>").append(UnicodeScript.\iof(codepoint).name()).append("</code><br/>\n");
        builder.append("Block: <code>").append(UnicodeBlock.\iof(codepoint).string).append("</code>");
    }
    
    shared String buildUrl(Referenceable model) {
        if (is Package model) {
            return buildUrl(model.\imodule) + ":" + model.nameAsString;
        }
        if (is Module model) {
            return model.nameAsString + "/" + model.version;
        }
        else if (is Declaration model) {
            String result = ":" + (model.name else "new");
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
                variable String? name = super.getSimpleDeclarationName(declaration, unit);
                if (!exists n = name, is Constructor declaration) {
                    name = "new";
                }
                if (exists n = name) {
                    value col = if (n.first?.lowercase else false) then ceylonHighlightingColors.identifier else ceylonHighlightingColors.type;
                    return buildLink(declaration, color(name, col));
                }
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
                builder.append(color("\"``text``\"", ceylonHighlightingColors.strings));
                
                // TODO display info for selected char? 
            } else if (is Tree.CharLiteral term, term.text.size > 2) {
                appendCharacterInfo(builder, term.text.span(1, 1));
            } else if (is Tree.NaturalLiteral term) {
                value text = term.text.replace("_", "");
                
                if (exists first = text.first, first == '#') {
                    builder.append(color(parseInteger(text.spanFrom(1), 16), ceylonHighlightingColors.number));
                } else if (exists first = text.first, first == '$') {
                    builder.append(color(parseInteger(text.spanFrom(1), 2), ceylonHighlightingColors.number));
                } else {
                    builder.append(color(parseInteger(text), ceylonHighlightingColors.number));
                }
            } else if (is Tree.FloatLiteral term) {
                builder.append(color(parseFloat(term.text.replace("_", "")), ceylonHighlightingColors.number));
            }
            
            return builder.string;
        }
        
        return null;
    }
    
    // see getInferredTypeHoverText(Node node, IProject project)
    String? getInferredTypeText(Tree.LocalModifier node, Project project) {
        if (exists model = node.typeModel) {
            return "Inferred type: <code>``printer.print(model, node.unit)``</code>";
        }
        
        return null;
    }
    
    void addPackage(Declaration decl, StringBuilder builder) {
        Package? pkg = (decl of Referenceable).unit.\ipackage;
        if (exists pkg, !pkg.qualifiedNameString.empty) {
            addIcon(builder, IconLoader.getIcon("/icons/package.png"));
            builder.append("Member of package ``buildLink(pkg, pkg.qualifiedNameString)``<br/>\n<br/>\n");
        }
    }
    
    void addSignature(Declaration decl, StringBuilder builder, Node node, Project project) {
        value annotations = StringBuilder();
        if (decl.shared) { annotations.append("shared "); }
        if (decl.actual) { annotations.append("actual "); }
        if (decl.default) { annotations.append("default "); }
        if (decl.formal) { annotations.append("formal "); }
        if (is Value decl, decl.late) { annotations.append("late "); }
        if (is TypedDeclaration decl, decl.variable) { annotations.append("variable "); }
        if (decl.native) { annotations.append("native "); }
        if (is TypeDeclaration decl) {
            if (decl.sealed) { annotations.append("sealed "); }
            if (decl.final) { annotations.append("final "); }
            if (is Class decl, decl.abstract) { annotations.append("abstract "); }
        }
        if (decl.annotation) { annotations.append("annotation "); }
       
        if (annotations.length() > 0) {
            addIcon(builder, AllIcons.Gutter.\iExtAnnotation);
            builder.append(color(annotations.string, ceylonHighlightingColors.annotation));
            builder.append("\n");
        }
       
        if (is Class decl) {
            if (decl.anonymous) {
                addIcon(builder, PlatformIcons.\iANONYMOUS_CLASS_ICON);
                builder.append(color("object ", ceylonHighlightingColors.keyword));
            } else {
                addIcon(builder, PlatformIcons.\iCLASS_ICON);
                builder.append(color("class ", ceylonHighlightingColors.keyword));
            }
        } else if (is Interface decl) {
            addIcon(builder, PlatformIcons.\iINTERFACE_ICON);
            builder.append(color("interface ", ceylonHighlightingColors.keyword));
        } else if (is TypeAlias decl) {
            builder.append(color("alias ", ceylonHighlightingColors.keyword));
        } else if (is Constructor decl) {
            builder.append(color("new ", ceylonHighlightingColors.keyword));
        } else if (is TypedDeclaration decl) {
            addTypedDeclarationSignature(decl, builder);
        }
        
        builder.append("<b>`` decl.name else "" ``</b>");
        
        addTypeParameters(decl, builder);
        addParameters(decl, builder, node);
        addInheritance(decl, builder);
    }
    
    void addIcon(StringBuilder builder, Icon icon, Integer leftPadding = 0) {
        if (leftPadding > 0) {
            builder.append("&nbsp".repeat(leftPadding));
        }
        builder.append("<img src='").append(icon.string).append("'/> ");
    }

    void addDoc(Annotated&Referenceable decl, StringBuilder builder, Scope? scope, Project project) {
        value doc = CeylonIterable(decl.annotations).find((ann) => ann.name.equals("doc") || ann.name.empty);
        
        if (exists doc, !doc.positionalArguments.empty) {
            value string = markdown(doc.positionalArguments.get(0).string, project, scope, decl.unit);
            builder.append("<p>\n").append(string).append("</p>");
        }
    }
    
    Scope resolveScope(Declaration decl) {
        if (is Scope decl) {
            return decl;
        } else {
            return decl.container;
        }
    }
    
    String markdown(String text, Project project, Scope? linkScope = null, Unit? unit = null) {
        value builder = Configuration.builder().forceExtentedProfile();
        builder.setCodeBlockEmitter(CeylonBlockEmitter(project));
        if (exists linkScope, exists unit) {
            builder.setSpecialLinkEmitter(CeylonSpanEmitter(linkScope, unit));
        } else {
            builder.setSpecialLinkEmitter(unlinkedSpanEmitter);
        }
        
        return Processor.process(text, builder.build());
    }
    
    void addInheritance(Declaration decl, StringBuilder builder) {
        value typeDecl = if (is TypedDeclaration decl, exists td = decl.typeDeclaration, td.anonymous) then td else decl;
        
        if (is TypeDeclaration typeDecl) {
            value unit = (decl of Referenceable).unit;
            
            if (exists cases = typeDecl.type.caseTypes) {
                builder.append("\n");
                addIcon(builder, AllIcons.General.\iOverridenMethod, 2);
                builder.append(color("of ", ceylonHighlightingColors.keyword))
                        .append(" | ".join(CeylonIterable(cases).map((c) => printer.print(c, unit))));
                
                // FIXME compilation error
                // see https://github.com/ceylon/ceylon-compiler/issues/2222
                //if (exists it = decl.selfType) {
                //    builder.append(" (self type)");
                //}
            }
            
            if (is Class typeDecl) {
                if (exists sup = typeDecl.extendedType) {
                    builder.append("\n");
                    addIcon(builder, AllIcons.General.\iOverridingMethod, 2);
                    builder.append(color("extends ", ceylonHighlightingColors.keyword))
                            .append(printer.print(sup, unit));
                }
            }
            
            if (!typeDecl.satisfiedTypes.empty) {
                builder.append("\n");
                addIcon(builder, AllIcons.General.\iImplementingMethod, 2);
                builder.append(color("satisfies ", ceylonHighlightingColors.keyword))
                        .append(" &amp; ".join(CeylonIterable(typeDecl.satisfiedTypes).map((s) => printer.print(s, unit))));
            }
        }
    }
    
    void addTypedDeclarationSignature(TypedDeclaration decl, StringBuilder builder) {
        value sequenced = isSequenced(decl);
        
        value unit = (decl of Referenceable).unit;
        value type = getType(decl, sequenced, unit);
       
        if (decl.dynamicallyTyped) {
            builder.append(color("dynamic", ceylonHighlightingColors.keyword));
        } else if (is Value decl, type.declaration.anonymous, !type.typeConstructor) {
            builder.append(color("object", ceylonHighlightingColors.keyword));
        } else if (is Function decl) {
            if (decl.declaredVoid) {
                builder.append(color("void", ceylonHighlightingColors.keyword));
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
    String getDeclarationDoc(Declaration model, Node node, Project project) {
        variable value decl = model;
        if (is Value model) {
            TypeDeclaration? typeDecl = model.typeDeclaration;
            
            if (exists typeDecl, typeDecl.anonymous, !model.type.typeConstructor) {
                decl = typeDecl;
            }
        }
        
        value builder = StringBuilder();
        
        builder.append("<pre>");
        addSignature(decl, builder, node, project);
        builder.append("</pre>\n");
        addPackage(decl, builder);
        addDoc(decl, builder, resolveScope(model), project);
        addParametersDoc(decl, builder);
        // TODO addClassMembers
        // TODO addNothingTypeInfo
        addUnit(decl, builder);
        
        return builder.string;
    }
    
    void addUnit(Declaration decl, StringBuilder builder) {
        builder.append("<br/>\n");
        addIcon(builder, IconLoader.getIcon("/icons/ceylonFile.png"));
        builder.append("&nbsp;Declared in ").append(decl.unit.filename);

        builder.append("<br/>\n");
        value mod = decl.unit.\ipackage.\imodule;
        addIcon(builder, AllIcons.Nodes.\iArtifact);
        if (mod.nameAsString.empty || mod.nameAsString.equals("default")) {
            builder.append("&nbsp;Belongs to default module.");
        } else {
            builder.append("&nbsp;Belongs to ").append(buildLink(mod, mod.nameAsString))
                    .append(color(" \"``mod.version``\"", ceylonHighlightingColors.strings));
        }
    }
                
    shared String? getDocumentationText(Referenceable model, Node node, Project project) {
        if (is Declaration model) {
            return getDeclarationDoc(model, node, project);
        } else if (is Package model) {
            return getPackageDoc(model, node, project);
        } else if (is Module model) {
            return getModuleDoc(model, node, project);
        }
        
        return null;
    }
    
    shared String getPackageDoc(Package pack, Node node, Project project) {
        value builder = StringBuilder();
        
        if (pack.shared) {
            builder.append(color("shared ", ceylonHighlightingColors.annotation));
        }
        
        builder.append(color("package ", ceylonHighlightingColors.keyword)).append(pack.nameAsString).append("<br/>\n");
        
        addDoc(pack, builder, pack, project);

        value mod = pack.\imodule;
        if (mod.java) {
            builder.append("<p>This package is implemented in Java.</p>\n");
        }
        if (JDKUtils.isJDKModule(mod.nameAsString)) {
            builder.append("<p>This package forms part of the Java SDK.</p>\n");             
        }
        
        // TODO? members
        
        builder.append("<br/>\n");
        addIcon(builder, AllIcons.Nodes.\iArtifact);

        if (mod.nameAsString.empty || mod.nameAsString.equals("default")) {
            builder.append("in default module\n");             
        } else {
            value version = "\"``mod.version``\"";
            builder.append("in module ").append(buildLink(mod, mod.nameAsString)).append(" ").append(color(version, ceylonHighlightingColors.strings));
        }
        
        return builder.string;
    }

    shared String? getModuleDoc(Module mod, Node node, Project project) {
        value builder = StringBuilder();

        builder.append(color("module ", ceylonHighlightingColors.keyword))
                .append(mod.nameAsString)
                .append(color(" \"``mod.version``\"", ceylonHighlightingColors.strings))
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

        addDoc(mod, builder, mod.getPackage(mod.nameAsString), project);
        // TODO? members
        
        return builder.string;
    }

    shared Node? getHoverNode(Tree.CompilationUnit rootNode, Integer offset) {
        return nodes.findNode(rootNode, offset);
    }    
    
    // see getHoverText(CeylonEditor editor, IRegion hoverRegion)
    shared JString? getDocumentation(Tree.CompilationUnit rootNode, Integer offset, Project project) {
        value node = getHoverNode(rootNode, offset);
        variable String? doc = null;
        
        if (exists node) {
            if (is Tree.LocalModifier node) {
                doc = getInferredTypeText(node, project);
            } else if (is Tree.Literal node) {
                doc = getTermTypeText(node);
            } else {
                Referenceable? model = nodes.getReferencedDeclaration(node);
                
                if (exists model) {
                    doc = getDocumentationText(model, node, project);
                }
            }
        }
        
        if (exists str = doc) {
            return javaString(str);
        }
        
        return null;
    }
}

