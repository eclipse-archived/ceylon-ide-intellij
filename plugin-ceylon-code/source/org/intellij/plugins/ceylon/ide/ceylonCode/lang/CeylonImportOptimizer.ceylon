import com.intellij.lang {
    ImportOptimizer
}
import com.intellij.openapi.editor.colors {
    EditorColorsManager,
    EditorFontType
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    DialogWrapper
}
import com.intellij.psi {
    PsiFile
}
import com.intellij.ui {
    ScrollPaneFactory
}
import com.intellij.ui.components {
    JBList
}
import com.intellij.usageView {
    UsageTreeColorsScheme,
    UsageTreeColors
}
import com.intellij.util {
    NotNullFunction
}
import com.redhat.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.awt {
    BorderLayout,
    Dimension
}
import java.lang {
    Runnable,
    JavaString=String
}

import javax.swing {
    ListSelectionModel,
    DefaultListModel,
    JPanel,
    JComponent,
    JLabel
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.ceylonCode.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.ceylonCode.psi {
    CeylonFile,
    descriptions
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class CeylonImportOptimizer()
        satisfies AbstractImportsCleaner & ImportOptimizer {

    variable Project? project = null;

    shared actual Runnable processFile(PsiFile psiFile) {
        value doc = psiFile.viewProvider.document;
        project = psiFile.project; //YUCK!!!
        assert (is CeylonFile psiFile);
        value cu = psiFile.compilationUnit;
        
        return object satisfies Runnable {
            shared actual void run() {
                cleanImports(cu, IdeaDocument(doc));
            }
        };
    }
    
    supports(PsiFile? psiFile) => psiFile is CeylonFile;

    shared actual Declaration? select(List<Declaration> proposals) {
        assert (exists p = project);
        value dialog = DeclarationChooserDialog(p, proposals);
        dialog.title = "Resolve Missing Import";
        dialog.modal = true;
//        dialog.setUndecorated(true);
        return dialog.select();
    }
}

class DeclarationChooserDialog(Project project, List<Declaration> declarations)
        extends DialogWrapper(project,true) {

    value model = DefaultListModel<Declaration>();
    value list = JBList(model);
    value panel = JPanel(BorderLayout(5,5));
    panel.minimumSize = Dimension(500, 100);
    list.selectionMode = ListSelectionModel.singleSelection;
    declarations.each(model.addElement);
    value font = EditorColorsManager.instance.globalScheme.getFont(EditorFontType.plain);
    value packageColor = UsageTreeColorsScheme.instance.scheme.getAttributes(UsageTreeColors.numberOfUsages);

    list.installCellRenderer(object satisfies NotNullFunction<Declaration, JComponent> {
        shared actual JLabel fun(Declaration dec) {
            value coloredText
                = "<html>"
                + highlighter.highlight(descriptions.descriptionForDeclaration(dec), project)
                + highlighter.toColoredHtml(" (``dec.unit.\ipackage.nameAsString``)", packageColor)
                + "</html>";
            value label = JLabel(coloredText, icons.forDeclaration(dec), JLabel.leading);
            label.font = outer.font;
            return label;
        }
    });

    shared actual JComponent createCenterPanel() {
        assert (exists first = declarations.first);
        value message = "Select package of unimported declaration '``first.name``':";
        value label = JLabel(highlighter.highlightQuotedMessage(message, project));
        panel.add(label, JavaString(BorderLayout.north));
        panel.add(ScrollPaneFactory.createScrollPane(list), JavaString(BorderLayout.center));
        return panel;
    }

    shared Declaration? select() {
        init();
        if (showAndGet()) {
            assert (is Declaration selection = list.selectedValue);
            return selection;
        }
        else {
            return null;
        }
    }

    preferredFocusedComponent => list;
}
