import com.intellij.lang {
    ImportOptimizer
}
import com.intellij.psi {
    PsiFile
}
import com.redhat.ceylon.ide.common.imports {
    AbstractImportsCleaner
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration
}

import java.lang {
    Runnable
}

import org.intellij.plugins.ceylon.ide.platform {
    IdeaDocument
}
import org.intellij.plugins.ceylon.ide.psi {
    CeylonFile
}

shared class CeylonImportOptimizer()
        satisfies AbstractImportsCleaner & ImportOptimizer {

//    variable Project? project = null;

    shared actual Runnable processFile(PsiFile psiFile) {
        assert (exists doc = psiFile.viewProvider.document,
                is CeylonFile psiFile);
//        project = psiFile.project; //YUCK!!!
        return object satisfies CollectingInfoRunnable {
            shared actual variable String? userNotificationInfo = null;
            shared actual void run() {
                if (exists rootNode
                        = psiFile.localAnalyzer?.result?.typecheckedRootNode) {
                    userNotificationInfo
                            = cleanImports(rootNode, IdeaDocument(doc))
                            then "Imports optimized"
                            else "Nothing to optimize";
                }
                else {
                    userNotificationInfo
                            = "Optimize imports not available";
                }
            }
        };
    }
    
    supports(PsiFile? psiFile) => psiFile is CeylonFile;

    shared actual Declaration? select(List<Declaration> proposals) {
        /*assert (exists p = project);
        value dialog = DeclarationChooserDialog(p, proposals);
        dialog.title = "Resolve Missing Import";
        dialog.modal = true;
        //dialog.setUndecorated(true);
        return dialog.select();*/
        return null;
    }
}

/*class DeclarationChooserDialog(Project project, List<Declaration> declarations)
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
}*/
