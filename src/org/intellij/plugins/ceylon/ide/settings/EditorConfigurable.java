package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.UIUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.CeylonSettings;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.settings.CeylonConfigurable.selectConfigurable;

public class EditorConfigurable extends BaseConfigurable {
    private JPanel myPanel;
    private JLabel completionLink;
    private JFormattedTextField modelUpdateDelay;

    public EditorConfigurable() {
        setupLinks();
        UIUtil.configureNumericFormattedTextField(modelUpdateDelay);
    }

    @Nls
    @Override
    public String getDisplayName() {
        return null;
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
        CeylonSettings.getInstance()
                .setAutoUpdateInterval(Integer.valueOf(modelUpdateDelay.getText()) * 1000);
    }

    @Override
    public void reset() {
        modelUpdateDelay.setText(String.valueOf(
                CeylonSettings.getInstance().getAutoUpdateInterval() / 1000));
    }

    @Override
    public boolean isModified() {
        return CeylonSettings.getInstance()
                .getAutoUpdateInterval() != Integer.valueOf(modelUpdateDelay.getText()) * 1000;
    }

    @Override
    public void disposeUIResources() {

    }

    private void setupLinks() {
        JLabelLinkListener listener = new JLabelLinkListener() {
            @Override
            public void onLinkClicked(String href) {
                selectConfigurable(href);
            }
        };

        List<JLabel> links = Collections.singletonList(completionLink);

        for (JLabel link : links) {
            link.addMouseListener(listener);
            link.addMouseMotionListener(listener);
        }
    }
}
