import com.github.rjeschke.txtmark {
    Processor,
    Configuration
}
import com.intellij.codeInsight.javadoc {
    JavaDocInfoGenerator
}
import com.intellij.openapi.editor {
    Document
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.psi.impl.compiled {
    ClsClassImpl,
    ClsMethodImpl
}
import com.intellij.psi.javadoc {
    PsiDocComment
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Node,
    Tree
}
import com.redhat.ceylon.ide.common.doc {
    DocGenerator,
    Colors,
    Icons
}
import com.redhat.ceylon.ide.common.imports {
    AbstractModuleImportUtil
}
import com.redhat.ceylon.ide.common.model {
    BaseCeylonProject
}
import com.redhat.ceylon.ide.common.settings {
    CompletionOptions
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult,
    IdePhasedUnit
}
import com.redhat.ceylon.ide.common.util {
    FindReferencedNodeVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration,
    Package,
    Module,
    Constructor,
    Unit,
    Scope,
    Function
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}

import java.awt {
    Font
}
import java.lang {
    JStringBuilder=StringBuilder
}
import java.util {
    JList=List
}

import javax.swing {
    Icon
}

import org.antlr.runtime {
    CommonToken
}
import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    ceylonHighlightingColors,
    highlight,
    textAttributes
}
import org.intellij.plugins.ceylon.ide.ceylonCode.imports {
    ideaModuleImportUtils
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    IdeaJavaModelAware
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    ideaIcons
}

String psiProtocol = "psi_element://";

String(String, Project) outerHighlight = highlight;

shared class IdeaDocGenerator(TypeChecker tc) satisfies DocGenerator<Document> {

    shared class DocParams(PhasedUnit pu, Project p) satisfies LocalAnalysisResult<Document> {
        assert(is IdePhasedUnit pu);

        shared actual Tree.CompilationUnit lastCompilationUnit => pu.compilationUnit;
        shared actual Tree.CompilationUnit parsedRootNode => lastCompilationUnit;
        shared actual Tree.CompilationUnit? typecheckedRootNode => lastCompilationUnit;
        shared actual PhasedUnit lastPhasedUnit => pu;
        shared actual Document document => nothing;
        shared actual JList<CommonToken>? tokens => pu.tokens;
        shared actual TypeChecker typeChecker => tc;
        shared actual BaseCeylonProject? ceylonProject => pu.moduleSourceMapper?.ceylonProject;
        shared Project ideaProject => p;
        shared actual CompletionOptions options => nothing;
    }

    String hexColor(Integer red, Integer green, Integer blue) {
        return "#" + formatInteger(red, 16).padLeading(2, '0') + formatInteger(green, 16).padLeading(2, '0') + formatInteger(blue, 16).padLeading(2, '0');
    }

    TextAttributesKey getAttributes(Colors color) {
        switch (color)
        case (Colors.strings) { return ceylonHighlightingColors.strings; }
        case (Colors.annotationStrings) { return ceylonHighlightingColors.annotationString; }
        case (Colors.numbers) { return ceylonHighlightingColors.number; }
        case (Colors.annotations) { return ceylonHighlightingColors.annotation; }
        case (Colors.keywords) { return ceylonHighlightingColors.keyword; }
        case (Colors.identifiers) { return ceylonHighlightingColors.identifier; }
        case (Colors.types) { return ceylonHighlightingColors.type; }
    }

    shared actual String color(Object? what, Colors how) {
        value attributes = textAttributes(getAttributes(how));
        value color = "color:``hexColor(attributes.foregroundColor.red, attributes.foregroundColor.green, attributes.foregroundColor.blue)``";
        value bold = if (attributes.fontType.and(Font.\iBOLD) != 0) then "font-weight: bold" else "";
        value italic = if (attributes.fontType.and(Font.\iITALIC) != 0) then "font-size: italic" else "";

        return "<code style='``color``; ``bold``; ``italic``'>``what else "<error>"``</code>";
    }

    Icon? getIconUrl(Icons|Referenceable thing) {
        if (is Declaration thing) {
            return ideaIcons.forDeclaration(thing);
        } else if (is Referenceable thing) {
            return switch (thing)
                case (is Module) ideaIcons.modules
                case (is Package) ideaIcons.packages
                else null;
        } else {
            return switch (thing)
                case (Icons.imports) ideaIcons.imports
                case (Icons.annotations) ideaIcons.annotations
                case (Icons.modules) ideaIcons.modules
                case (Icons.objects) ideaIcons.objects
                case (Icons.classes) ideaIcons.classes
                case (Icons.interfaces) ideaIcons.interfaces
                case (Icons.enumeration) ideaIcons.enumerations
                case (Icons.extendedType) ideaIcons.extendedType
                case (Icons.satisfiedTypes) ideaIcons.satisfiedTypes
                case (Icons.exceptions) ideaIcons.exceptions
                case (Icons.see) ideaIcons.see
                case (Icons.implementation) ideaIcons.satisfiedTypes
                case (Icons.override) ideaIcons.extendedType
                case (Icons.returns) ideaIcons.returns
                case (Icons.units) ideaIcons.file
                case (Icons.parameters) ideaIcons.param
                case (Icons.attributes) ideaIcons.attributes
                case (Icons.types) ideaIcons.types
                else null;
        }
    }

    shared actual void addIconAndText(StringBuilder builder, Icons|Referenceable icon, String text) {
        value iconUrl = getIconUrl(icon);

        if (exists iconUrl) {
            builder.append("<div style='background: url(" + iconUrl.string + ") left 10px no-repeat; padding-left: 16px'>");
        } else {
            builder.append("<div>");
        }

        builder.append(text).append("</div>");
    }

    void appendDocSection(StringBuilder buffer, PsiDocComment doc) {
        value generator = JavaDocInfoGenerator(doc.project, doc);
        value builder = JStringBuilder();
        generator.generateCommonSection(builder, doc);
        buffer.append(builder.string);
    }

    shared actual void appendJavadoc(Declaration model, StringBuilder buffer) {
        value declaration = if (is Function model, model.annotation)
                            then model.typeDeclaration
                            else model;

        if (is IdeaJavaModelAware unit = declaration.unit,
            exists javaEl = unit.toJavaElement(declaration)) {

            if (exists doc = javaEl.docComment) {
                appendDocSection(buffer, doc);
            } else if (is ClsClassImpl javaEl,
                       exists source = javaEl.sourceMirrorClass,
                       exists doc = source.docComment) {
                appendDocSection(buffer, doc);
            } else if (is ClsMethodImpl javaEl,
                       exists source = javaEl.sourceMirrorMethod,
                       exists doc = source.docComment) {
                appendDocSection(buffer, doc);
            }
        }
    }

    shared actual String highlight(String text, LocalAnalysisResult<Document> cmp) {
        assert (is DocParams cmp);
        return outerHighlight(text, cmp.ideaProject);
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
    
    shared actual String markdown(String text, LocalAnalysisResult<Document> cmp, Scope? linkScope, Unit? unit) {
        assert (is DocParams cmp);
        value project = cmp.ideaProject;
        value builder = Configuration.builder().forceExtentedProfile();
        builder.setCodeBlockEmitter(CeylonBlockEmitter(project));
        
        if (exists linkScope, exists unit) {
            builder.setSpecialLinkEmitter(CeylonSpanEmitter(linkScope, unit, buildUrl));
        } else {
            builder.setSpecialLinkEmitter(unlinkedSpanEmitter);
        }
        
        return Processor.process(text, builder.build());
    }
    
    shared actual String buildLink(Referenceable|String model, String text,
        String protocol) {
        
        value href = if (is Referenceable model) then buildUrl(model) else model;
        return "<a href=\"``psiProtocol````protocol``:``href``\">``text``</a>";
    }

    class MyPrinter(Boolean abbreviate)
            extends TypePrinter(abbreviate, true, false, true, false) {
        
        shared actual String getSimpleDeclarationName(Declaration? declaration, Unit unit) {
            if (exists declaration) {
                variable String? name = super.getSimpleDeclarationName(declaration, unit);
                if (!exists n = name, is Constructor declaration) {
                    name = "new";
                }
                
                if (exists n = name) {
                    value col = if (n.first?.lowercase else false) then Colors.identifiers else Colors.types;
                    return buildLink(declaration, color(name, col));
                }
            }
            
            return "&lt;unknown&gt;";
        }
        
        amp() => "&amp;";
        lt() => "&lt;";
        gt() => "&gt;";
    }

    shared actual TypePrinter printer = MyPrinter(true);
    shared actual TypePrinter verbosePrinter = MyPrinter(false);

    showMembers => false;
    
    shared actual void appendPageProlog(StringBuilder builder) {
        value css = `module`.resourceByPath("ceylondoc.css");
        value style = if (exists css) then css.textContent() else "";
        builder.append("<html><head><style>``style``</style></head><body>");
    }
    
    shared actual void appendPageEpilog(StringBuilder builder) {
        builder.append("</body></html>");
    }
    
    getUnitName(Unit u) => u.filename;
    
    getLiveValue(Declaration dec, Unit unit) => null;
    
    shared actual Node? getReferencedNode(Declaration dec) {
        value relPath = dec.unit.relativePath;

        if (exists unit = tc.getPhasedUnitFromRelativePath(relPath)) {
            value visitor = FindReferencedNodeVisitor(dec);
            unit.compilationUnit.visit(visitor);
            return visitor.declarationNode;
        }
        
        return null;
    }
    
    getPhasedUnit(Unit u) => tc.getPhasedUnitFromRelativePath(u.relativePath);
    
    shared actual AbstractModuleImportUtil<out Anything,out Anything,out Anything,out Anything,out Anything,out Anything> moduleImportUtil
            => ideaModuleImportUtils;
    
    supportsQuickAssists => true;
}
