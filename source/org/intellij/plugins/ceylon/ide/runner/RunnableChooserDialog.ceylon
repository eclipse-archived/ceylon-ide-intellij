import com.intellij.openapi.project {
    Project
}
import com.redhat.ceylon.model.typechecker.model {
    Declaration,
    Class,
    Function
}

import java.awt {
    Component
}
import java.awt.event {
    MouseAdapter,
    MouseEvent
}

import javax.swing {
    DefaultListModel,
    DefaultListCellRenderer,
    JList
}

import org.intellij.plugins.ceylon.ide.highlighting {
    highlighter
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaModule
}
import org.intellij.plugins.ceylon.ide.util {
    icons
}

class RunnableChooserDialog(Project project, IdeaModule mod)
        extends AbstractRunnableChooserDialog(project) {

    init();
    title = "Select a Runnable Declaration";

    shared Declaration? selectedDeclaration => runnableList.selectedValue;

    Boolean isRunnable(Declaration decl) {
        switch (decl)
        case (is Class) {
            if (decl.objectClass) {
                return false;
            }
            value plist = decl.parameterList;
            return !plist exists
                || plist.parameters.empty;
        }
        case (is Function) {
            value plist = decl.firstParameterList;
            return plist exists
                && plist.parameters.empty;
        }
        else {
            return false;
        }
    }

    DefaultListModel<Declaration> createModel() {
        value model = DefaultListModel<Declaration>();
        for (pkg in mod.packages) {
            for (declaration in pkg.members) {
                if (declaration.toplevel
                    && declaration.shared
                    && isRunnable(declaration)) {
                    model.addElement(declaration);
                }
            }
        }
        return model;
    }

    runnableList.model = createModel();

    runnableList.setCellRenderer(object extends DefaultListCellRenderer() {
        shared actual Component getListCellRendererComponent(
                JList<out Object> list, Object val, Integer index,
                Boolean isSelected, Boolean cellHasFocus) {
            value cmp = super.getListCellRendererComponent(list, val, index, isSelected, cellHasFocus);
            if (is Declaration val) {
                icon = icons.forDeclaration(val);
                text = "<html>``highlighter.highlight(text, project)``</html>";
            }
            return cmp;
        }
    });

    okActionEnabled = false;

    runnableList.addListSelectionListener((e)
            => okActionEnabled = runnableList.selectedValue exists);

    runnableList.addMouseListener(object extends MouseAdapter() {
        shared actual void mouseClicked(MouseEvent e) {
            if (e.clickCount == 2,
                exists r = runnableList.getCellBounds(0, runnableList.lastVisibleIndex),
                r.contains(e.point) && runnableList.locationToIndex(e.point) != -1) {
                doOKAction();
            }
        }
    });

    preferredFocusedComponent => runnableList;

    createCenterPanel() => contentPane;

}