import com.intellij {
    CommonBundle
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    DialogWrapper,
    VerticalFlowLayout
}
import com.intellij.ui {
    ScrollPaneFactory
}
import com.intellij.ui.components {
    JBLabel,
    JBList
}
import com.intellij.util.ui {
    UIUtil,
    JBUI
}

import java.awt {
    BorderLayout
}
import java.lang {
    JString=String
}

import javax.swing {
    JPanel,
    JComponent,
    JButton
}

import org.intellij.plugins.ceylon.ide.correct {
    CeylonCellRenderer,
    CeylonListItem
}

class PasteImportsDialog(Project project, {CeylonListItem*} elements)
        extends DialogWrapper(project, true) {

    value myList = JBList(*elements);

    shared actual JComponent createCenterPanel() {
        title = "Select Declarations to Import";
        value panel = JPanel(BorderLayout(10, 4));
        myList.setCellRenderer(CeylonCellRenderer());
        this.myList.setSelectionInterval(0, elements.size - 1);
        panel.add(ScrollPaneFactory.createScrollPane(myList),
                  JString(BorderLayout.center));
        value label
            = JBLabel("<html>The pasted code fragment contains the following references to unimported declarations.<br/>
                       Selected declarations will be automatically added to the imports of this file.</html>",
                      UIUtil.ComponentStyle.small, UIUtil.FontColor.brighter);
        panel.add(label, JString(BorderLayout.north));
        value buttonPanel = JPanel(VerticalFlowLayout());
        value okButton = JButton(CommonBundle.okButtonText);
        rootPane.defaultButton = okButton;
        buttonPanel.add(okButton);
        value cancelButton = JButton(CommonBundle.cancelButtonText);
        buttonPanel.add(cancelButton);
        panel.preferredSize = JBUI.size(500, 400);
        return panel;
    }

//    dimensionServiceKey
//            => "#com.intellij.codeInsight.editorActions.RestoreReferencesDialog";

    shared List<CeylonListItem> selectedElements
            => [ for (it in myList.selectedValuesList)
                 if (is CeylonListItem it) it ];

    init();

}
