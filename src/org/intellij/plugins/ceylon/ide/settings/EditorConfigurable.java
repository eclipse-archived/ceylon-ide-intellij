package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.settings.CeylonConfigurable.selectConfigurable;

public class EditorConfigurable extends BaseConfigurable {
    private JPanel myPanel;
    private JLabel completionLink;

    public EditorConfigurable() {
        setupLinks();
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

    }

    @Override
    public void reset() {

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
