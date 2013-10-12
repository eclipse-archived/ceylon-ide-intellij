package org.intellij.plugins.ceylon.sdk;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.ui.ComboBox;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class CeylonSdkAdditionalDataConfigurable implements AdditionalDataConfigurable {

    private Sdk sdk;
    private JComboBox jdkComboBox = new ComboBox();
    private JPanel contentPanel = new JPanel();
    private SdkModel sdkModel;
    private SdkModificator sdkModificator;

    public CeylonSdkAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        this.sdkModel = sdkModel;
        this.sdkModificator = sdkModificator;

        updateJdkList();
    }

    private void updateJdkList() {
        jdkComboBox.removeAllItems();

        for (Sdk mySdk : sdkModel.getSdks()) {
            if (mySdk.getSdkType() instanceof JavaSdk) {
                // TODO check JDK version
                jdkComboBox.addItem(mySdk);
            }
        }

        Sdk internalSdk = CeylonSdk.getInternalSdk(sdk);
        if (sdk != null && internalSdk != null) {
            for (int i = 0; i < jdkComboBox.getItemCount(); i++) {
                if (((Sdk)jdkComboBox.getItemAt(i)).getName().equals(internalSdk.getName())) {
                    jdkComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
    }

    @Override
    public void setSdk(Sdk sdk) {
        this.sdk = sdk;
        updateJdkList();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        return contentPanel;
    }

    @Override
    public boolean isModified() {
        Sdk selectedItem = (Sdk) jdkComboBox.getSelectedItem();
        Sdk internalSdk = CeylonSdk.getInternalSdk(sdk);
        return (internalSdk == null) || !selectedItem.getName().equals(internalSdk.getName());
    }

    @Override
    public void apply() throws ConfigurationException {
        SdkAdditionalData additionalData = sdk.getSdkAdditionalData();

        if (additionalData instanceof CeylonSdkAdditionalData) {
            ((CeylonSdkAdditionalData) additionalData).setJavaSdk((Sdk) jdkComboBox.getSelectedItem());
        }
    }

    @Override
    public void reset() {
        updateJdkList();
    }

    @Override
    public void disposeUIResources() {
    }

    private void createUIComponents() {
    }
}
