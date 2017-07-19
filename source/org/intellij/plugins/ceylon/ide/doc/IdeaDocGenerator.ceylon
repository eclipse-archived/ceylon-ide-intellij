import com.github.rjeschke.txtmark {
    Processor,
    Configuration
}
import com.intellij.codeInsight.javadoc {
    JavaDocInfoGenerator
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
import com.intellij.ui {
    JBColor
}
import com.intellij.util.concurrency {
    FixedFuture
}
import com.redhat.ceylon.compiler.typechecker {
    TypeChecker
}
import com.redhat.ceylon.compiler.typechecker.context {
    PhasedUnit
}
import com.redhat.ceylon.compiler.typechecker.tree {
    Tree
}
import com.redhat.ceylon.ide.common.doc {
    DocGenerator,
    Colors,
    Icons
}
import com.redhat.ceylon.ide.common.typechecker {
    LocalAnalysisResult,
    IdePhasedUnit
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
    Font {
        bold=\iBOLD,
        italic=\iITALIC
    }
}
import java.lang {
    JStringBuilder=StringBuilder,
    NoClassDefFoundError
}

import javax.swing {
    Icon
}

import org.intellij.plugins.ceylon.ide.highlighting {
    ceylonHighlightingColors,
    highlighter,
    textAttributes
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaJavaModelAware
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

String psiProtocol = "psi_element://";

shared class IdeaDocGenerator(TypeChecker typechecker)
        satisfies DocGenerator {

    shared class DocParams(lastPhasedUnit, ideaProject)
            satisfies LocalAnalysisResult {

        shared actual PhasedUnit lastPhasedUnit;
        assert(is IdePhasedUnit lastPhasedUnit);
        shared Project ideaProject;
        shared actual Tree.CompilationUnit lastCompilationUnit
                => lastPhasedUnit.compilationUnit;

        parsedRootNode => lastCompilationUnit;
        tokens => lastPhasedUnit.tokens;
        typeChecker => outer.typechecker;
        ceylonProject => lastPhasedUnit.moduleSourceMapper?.ceylonProject;
        shared actual Nothing commonDocument {
            "should never be used to generate the doc"
            assert(false);
        }

        phasedUnitWhenTypechecked => FixedFuture(lastPhasedUnit);

        typecheckedPhasedUnit => lastPhasedUnit;

    }

    String hexColor(Integer red, Integer green, Integer blue) 
            => "#"
            + Integer.format(red, 16).padLeading(2, '0')
            + Integer.format(green, 16).padLeading(2, '0')
            + Integer.format(blue, 16).padLeading(2, '0');

    TextAttributesKey getAttributes(Colors color) 
            => switch (color)
            case (Colors.strings) ceylonHighlightingColors.strings
            case (Colors.annotationStrings) ceylonHighlightingColors.annotationString
            case (Colors.numbers) ceylonHighlightingColors.number
            case (Colors.annotations) ceylonHighlightingColors.annotation
            case (Colors.keywords) ceylonHighlightingColors.keyword
            case (Colors.identifiers) ceylonHighlightingColors.identifier
            case (Colors.types) ceylonHighlightingColors.type;

    shared actual String color(Object? what, Colors how) {
        value attributes = textAttributes(getAttributes(how));
        value foregroundColor
                = attributes.foregroundColor
                else JBColor.foreground();
        value color = hexColor {
            red = foregroundColor.red;
            green = foregroundColor.green;
            blue = foregroundColor.blue;
        };
        value fontBold = attributes.fontType.and(bold) != 0 then "font-weight:bold;" else "";
        value fontItalic = attributes.fontType.and(italic) != 0 then "font-style:italic;" else "";

        return "<code style='color:``color``;``fontBold````fontItalic``'>``what else "<error>"``</code>";
    }

    Icon? getIconUrl(Icons|Referenceable thing) 
            => switch (thing)
            //models:
            case (is Declaration) icons.getBaseIcon(thing)
            case (is Module) icons.moduleFolders
            case (is Package) icons.packageFolders
            //icons:
            case (Icons.imports) icons.singleImport
            case (Icons.annotations) icons.annotations
            case (Icons.modules) icons.moduleFolders
            case (Icons.objects) icons.objects
            case (Icons.classes) icons.classes
            case (Icons.interfaces) icons.interfaces
            case (Icons.enumeration) icons.enumerations
            case (Icons.extendedType) icons.extendedType
            case (Icons.satisfiedTypes) icons.satisfiedTypes
            case (Icons.exceptions) icons.exceptions
            case (Icons.see) icons.see
            case (Icons.implementation) icons.satisfiedTypes
            case (Icons.override) icons.extendedType
            case (Icons.returns) icons.returns
            case (Icons.units) icons.file
            case (Icons.parameters) icons.param
            case (Icons.attributes) icons.attributes
            case (Icons.types) icons.types
            else null;

    shared actual void addIconAndText(StringBuilder builder, Icons|Referenceable icon, String text) {
        if (exists iconUrl = getIconUrl(icon)) {
            builder.append("<div style='background: url(``iconUrl``) left 10px no-repeat; padding-left: 16px'>");
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
        value declaration
                = if (is Function model, model.annotation)
                then model.typeDeclaration
                else model;

        try {
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
        catch (Exception|NoClassDefFoundError e) {
            //workaround for Android Studio
            e.printStackTrace();
        }
    }

    shared actual String highlight(String text, LocalAnalysisResult cmp) {
        assert (is DocParams cmp);
        return highlighter.highlight(text, cmp.ideaProject);
    }

    shared String buildUrl(Referenceable model) {
        if (is Package model) {
            return buildUrl(model.\imodule) + "/" + model.nameAsString;
        }
        if (is Module model) {
            return model.nameAsString + "/" + model.version;
        }
        else if (is Declaration model) {
            value result = "/" + (model.name else "new");
            return if (is Referenceable container = model.container)
                then buildUrl(container) + result
                else result;
        }
        else {
            return "";
        }
    }
    
    shared actual String markdown(String text, LocalAnalysisResult cmp, Scope? linkScope, Unit? unit) {
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
        return "<a href='``psiProtocol````protocol``:``href``'>``text``</a>";
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

    printer = MyPrinter(true);
    verbosePrinter = MyPrinter(false);

    showMembers => false;
    
    shared actual void appendPageProlog(StringBuilder builder) {
        value css = `module`.resourceByPath("ceylondoc.css");
        builder.append("<html><head><style>``css?.textContent() else ""``</style></head><body>");
    }
    
    appendPageEpilog(StringBuilder builder) => builder.append("</body></html>");
    
    getLiveValue(Declaration dec, Unit unit) => null;

    supportsQuickAssists => true;
}
