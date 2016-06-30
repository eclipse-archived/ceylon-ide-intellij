package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.options.*;
import com.intellij.openapi.options.ex.Settings;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.CeylonSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import java.util.List;

/**
 * Settings for Ceylon.
 */
public class CeylonConfigurable extends BaseConfigurable {
    private JPanel myPanel;
    private JRadioButton jvmTarget;
    private JRadioButton jsTarget;
    private JRadioButton crossTarget;
    private JTextField sourceName;
    private JTextField resourceName;
    private JLabel editorLink;
    private JLabel completionLink;
    private JButton restoreDefaultsButton;
    private JLabel compilerLink;
    private ButtonGroup targetVM;

    public CeylonConfigurable() {
        setupLinks();
        restoreDefaultsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CeylonSettings.getInstance().loadState(new CeylonSettings.CeylonOptions());
                reset();
            }
        });
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Ceylon";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return myPanel;
    }

    @Override
    public void apply() throws ConfigurationException {
        CeylonSettings settings = CeylonSettings.getInstance();

        settings.setDefaultTargetVm(getTargetVm());
        settings.setDefaultSourceFolder(sourceName.getText());
        settings.setDefaultResourceFolder(resourceName.getText());
    }

    private String getTargetVm() {
        if (jvmTarget.isSelected()) {
            return "jvm";
        } else if (jsTarget.isSelected()) {
            return "js";
        }
        return "cross";
    }

    @Override
    public void reset() {
        CeylonSettings settings = CeylonSettings.getInstance();

        switch (settings.getDefaultTargetVm()) {
            case "jvm":
                targetVM.setSelected(jvmTarget.getModel(), true);
                break;
            case "js":
                targetVM.setSelected(jsTarget.getModel(), true);
                break;
            default:
                targetVM.setSelected(crossTarget.getModel(), true);
                break;
        }
        sourceName.setText(settings.getDefaultSourceFolder());
        resourceName.setText(settings.getDefaultResourceFolder());
    }

    @Override
    public boolean isModified() {
        CeylonSettings settings = CeylonSettings.getInstance();

        return !(
                settings.getDefaultTargetVm().equals(getTargetVm())
                && settings.getDefaultSourceFolder().equals(sourceName.getText())
                && settings.getDefaultResourceFolder().equals(resourceName.getText())
        );
    }

    @Override
    public void disposeUIResources() {

    }

    private void createUIComponents() {
    }

    private void setupLinks() {
        JLabelLinkListener listener = new JLabelLinkListener() {
            @Override
            public void onLinkClicked(String href) {
                selectConfigurable(href);
            }
        };

        List<JLabel> links = Arrays.asList(editorLink, completionLink, compilerLink);

        for (JLabel link : links) {
            link.addMouseListener(listener);
            link.addMouseMotionListener(listener);
        }
    }

    static void selectConfigurable(String id) {
        DataContext context = DataManager.getInstance().getDataContextFromFocus().getResult();
        if (context != null) {
            Settings settings = Settings.KEY.getData(context);
            if (settings != null) {
                settings.select(settings.find(id));
            }
        }
    }
}
