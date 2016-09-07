package org.intellij.plugins.ceylon.ide.settings;

import com.intellij.openapi.options.BaseConfigurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.util.ui.UIUtil;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

import static org.intellij.plugins.ceylon.ide.settings.CeylonConfigurable.selectConfigurable;

public class ModelUpdateConfigurable extends BaseConfigurable {
    private JPanel myPanel;
    private JLabel completionLink;
    private JFormattedTextField modelUpdateDelay;
    private JFormattedTextField modelUpdateTimeoutMinutes;
    private JCheckBox decreaseThePriorityOfCheckBox;

    public ModelUpdateConfigurable() {
//        setupLinks();
        UIUtil.configureNumericFormattedTextField(modelUpdateDelay);
        UIUtil.configureNumericFormattedTextField(modelUpdateTimeoutMinutes);
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
        ceylonSettings_.get_()
                .setAutoUpdateInterval(Integer.valueOf(modelUpdateDelay.getText()) * 1000);
        ceylonSettings_.get_()
                .setModelUpdateTimeoutMinutes(Integer.valueOf(modelUpdateTimeoutMinutes.getText()));
        ceylonSettings_.get_()
                .setLowerModelUpdatePriority(decreaseThePriorityOfCheckBox.isSelected());
    }

    @Override
    public void reset() {
        modelUpdateDelay.setText(String.valueOf(
                ceylonSettings_.get_().getAutoUpdateInterval() / 1000));
        modelUpdateTimeoutMinutes.setText(String.valueOf(
                ceylonSettings_.get_().getModelUpdateTimeoutMinutes()));
        decreaseThePriorityOfCheckBox.setSelected(
                ceylonSettings_.get_().getLowerModelUpdatePriority());
    }

    @Override
    public boolean isModified() {
        return ceylonSettings_.get_()
                .getAutoUpdateInterval() != Integer.valueOf(modelUpdateDelay.getText()) * 1000 ||
        ceylonSettings_.get_()
                .getModelUpdateTimeoutMinutes() != Integer.valueOf(modelUpdateTimeoutMinutes.getText()) ||
        ceylonSettings_.get_()
                .getLowerModelUpdatePriority() != decreaseThePriorityOfCheckBox.isSelected();
    }

    @Override
    public void disposeUIResources() {

    }

//    private void setupLinks() {
//        JLabelLinkListener listener = new JLabelLinkListener() {
//            @Override
//            public void onLinkClicked(String href) {
//                selectConfigurable(href);
//            }
//        };
//
//        List<JLabel> links = Collections.singletonList(completionLink);
//
//        for (JLabel link : links) {
//            link.addMouseListener(listener);
//            link.addMouseMotionListener(listener);
//        }
//    }
}
