import ceylon.interop.java {
    CeylonStringIterable
}
import java.lang {
    Types {
        nativeString
    }
}

import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.ui {
    DialogWrapper
}
import com.intellij.ui {
    ScrollPaneFactory,
    RawCommandLineEditor
}
import com.intellij.ui.components {
    JBTextField,
    JBLabel
}
import com.intellij.util.execution {
    ParametersListUtil {
        lineParser=defaultLineParser
    }
}
import com.intellij.util.ui {
    JBUI
}

import java.awt {
    GridBagLayout,
    GridBagConstraints,
    Dimension
}

import javax.swing {
    JComponent,
    JPanel
}

class CeylonBeforeRunEditor(Project project) extends DialogWrapper(project, false) {

    value panel = JPanel();
    value command = JBTextField();
    value parameters = RawCommandLineEditor();

    init();

    shared actual JComponent? createCenterPanel() {
        initForm();
        return JBUI.Panels.simplePanel(ScrollPaneFactory.createScrollPane(panel, true));
    }

    void initForm() {
        function constraints(Integer x, Integer y) {
            value c = GridBagConstraints();
            c.gridx = x;
            c.gridy = y;
            if (x > 0) {
                c.weightx = 1.0;
                c.fill = GridBagConstraints.horizontal;
            }
            c.anchor = GridBagConstraints.lineStart;
            return c;
        }

        value size = command.preferredSize;
        size.setSize(600f, size.height);

        panel.layout = GridBagLayout();
        panel.add(JBLabel("Command to run:"), constraints(0, 0));
        panel.add(command, constraints(1, 0));
        panel.add(JBLabel("Command parameters:"), constraints(0, 1));
        panel.add(parameters, constraints(1, 1));

        panel.preferredSize = Dimension(550, panel.preferredSize.height.integer);
    }

    shared void loadTask(CeylonBeforeRunTask task) {
        command.text = task.command;
        parameters.text = " ".join(task.parameters);
    }

    shared void updateTask(CeylonBeforeRunTask task) {
        task.command = command.text;
        task.parameters = CeylonStringIterable(lineParser.fun(nativeString(parameters.text)));
    }
}