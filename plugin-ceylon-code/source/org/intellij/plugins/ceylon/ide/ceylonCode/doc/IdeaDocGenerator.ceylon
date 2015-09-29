import com.github.rjeschke.txtmark {
    Processor,
    Configuration
}
import com.intellij.icons {
    AllIcons
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
import com.intellij.openapi.util {
    IconLoader
}
import com.intellij.util {
    PlatformIcons
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
import com.redhat.ceylon.ide.common.model {
    CeylonProject
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult
}
import com.redhat.ceylon.ide.common.util {
    FindReferencedNodeVisitor
}
import com.redhat.ceylon.model.typechecker.model {
    Referenceable,
    Declaration,
    Package,
    Module,
    Class,
    Interface,
    Constructor,
    Unit,
    Scope,
    Value,
    Function,
    TypeParameter
}
import com.redhat.ceylon.model.typechecker.util {
    TypePrinter
}

import java.awt {
    Font
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

String psiProtocol = "psi_element://";

String(String, Project) outerHighlight = highlight;

shared class IdeaDocGenerator(TypeChecker? tc) extends DocGenerator<Document,Nothing>() {

    shared class DocParams(PhasedUnit pu, Project p) satisfies LocalAnalysisResult<Document,Nothing> {
        shared actual Tree.CompilationUnit rootNode => pu.compilationUnit;
        shared actual PhasedUnit phasedUnit => pu;
        shared actual Document document => nothing;
        shared actual JList<CommonToken>? tokens => pu.tokens;
        shared actual TypeChecker typeChecker => nothing;
        shared actual CeylonProject<Nothing>? ceylonProject => nothing;
        shared Project ideaProject => p;
    }
    
    String hexColor(Integer red, Integer green, Integer blue) {
        return "#" + formatInteger(red, 16).padLeading(2, '0') + formatInteger(green, 16).padLeading(2, '0') + formatInteger(blue, 16).padLeading(2, '0');
    }
    
    TextAttributesKey getAttributes(Colors color) {
        switch (color)
        case (Colors.strings) { return ceylonHighlightingColors.strings; }
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
        if (is Referenceable thing) {
            return switch (thing)
            case (is Module) AllIcons.Nodes.\iArtifact
            case (is Package) IconLoader.getIcon("/icons/package.png")
            case (is Class) if (thing.anonymous) 
                then PlatformIcons.\iANONYMOUS_CLASS_ICON 
                else PlatformIcons.\iCLASS_ICON
            case (is Interface) PlatformIcons.\iINTERFACE_ICON
            case (is Constructor) PlatformIcons.\iCLASS_INITIALIZER
            case (is Value) PlatformIcons.\iFIELD_ICON
            case (is Function) if (thing.shared) then PlatformIcons.\iMETHOD_ICON else PlatformIcons.\iFUNCTION_ICON
            case (is TypeParameter) PlatformIcons.\iVARIABLE_ICON
            else if (is Declaration thing, thing.parameter) then PlatformIcons.\iPARAMETER_ICON
            else null;
        } else {
            return switch (thing)
            case (Icons.annotations) AllIcons.Gutter.\iExtAnnotation
            case (Icons.modules) AllIcons.Nodes.\iArtifact
            case (Icons.objects) PlatformIcons.\iANONYMOUS_CLASS_ICON
            case (Icons.classes) PlatformIcons.\iCLASS_ICON
            case (Icons.interfaces) PlatformIcons.\iINTERFACE_ICON
            case (Icons.enumeration) PlatformIcons.\iENUM_ICON
            case (Icons.extendedType) AllIcons.General.\iOverridingMethod
            case (Icons.satisfiedTypes) AllIcons.General.\iImplementingMethod
            case (Icons.exceptions) AllIcons.Nodes.\iExceptionClass
            case (Icons.see) AllIcons.Actions.\iShare
            case (Icons.implementation) AllIcons.General.\iOverridingMethod
            case (Icons.override) AllIcons.General.\iImplementingMethod
            case (Icons.returns) AllIcons.Actions.\iStepOut
            case (Icons.units) IconLoader.getIcon("/icons/ceylonFile.png")
            case (Icons.parameters) AllIcons.Nodes.\iParameter
            case (Icons.attributes) AllIcons.Nodes.\iParameter // TODO
            case (Icons.types) AllIcons.Nodes.\iParameter // TODO
            ;
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
    
    shared actual String getInitialValueDescription(Declaration d, LocalAnalysisResult<Document,Nothing> cmp) => "";

    shared actual void appendJavadoc(Declaration model, StringBuilder buffer) {}
        
    shared actual String highlight(String text, LocalAnalysisResult<Document,Nothing> cmp) {
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
    
    shared actual String markdown(String text, LocalAnalysisResult<Document,Nothing> cmp, Scope? linkScope, Unit? unit) {
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
    
    shared actual String buildLink(Referenceable model, String text, String protocol) {
        return "<a href=\"``psiProtocol````protocol``:``buildUrl(model)``\">``text``</a>";
    }

    shared actual object printer extends TypePrinter(true, true, false, true, false) {
        
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
        
        shared actual String amp() => "&amp;";
        shared actual String lt() => "&lt;";
        shared actual String gt() => "&gt";
    }

    shared actual Boolean showMembers => false;
    
    shared actual void appendPageProlog(StringBuilder builder) {
        value css = `module`.resourceByPath("ceylondoc.css");
        value style = if (exists css) then css.textContent() else "";
        builder.append("<html><head><style>``style``</style></head><body>");
    }
    
    shared actual void appendPageEpilog(StringBuilder builder) {
        builder.append("</body></html>");
    }
    
    shared actual String getUnitName(Unit u) => u.filename;
    
    shared actual String? getLiveValue(Declaration dec, Unit unit) => null;
    
    shared actual Node? getReferencedNode(Declaration dec) {
        value relPath = dec.unit.relativePath;

        if (exists tc, exists unit = tc.getPhasedUnitFromRelativePath(relPath)) {
            value visitor = FindReferencedNodeVisitor(dec);
            unit.compilationUnit.visit(visitor);
            return visitor.declarationNode;
        }
        
        return null;
    }
    
    shared actual PhasedUnit? getPhasedUnit(Unit u) {
        if (exists tc) { 
            return tc.getPhasedUnitFromRelativePath(u.relativePath);
        }
        
        return null;
    }
}
