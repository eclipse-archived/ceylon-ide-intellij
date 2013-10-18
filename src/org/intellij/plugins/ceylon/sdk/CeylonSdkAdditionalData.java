package org.intellij.plugins.ceylon.sdk;

import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkModel;
import com.intellij.openapi.projectRoots.ValidatableSdkAdditionalData;
import com.intellij.openapi.util.DefaultJDOMExternalizer;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.JDOMExternalizable;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@State(
        name = "CeylonSDK",
        storages = {
                @Storage(file = StoragePathMacros.PROJECT_FILE)
        }
)
public class CeylonSdkAdditionalData implements ValidatableSdkAdditionalData, JDOMExternalizable {

    private CeylonSdkOptions myOptions = new CeylonSdkOptions();

    public CeylonSdkAdditionalData() {

    }

    public CeylonSdkAdditionalData(Sdk sdk, String ceylonVersion) {
        myOptions.myJavaSdk = sdk;
        myOptions.myCeylonVersion = ceylonVersion;
    }

    @SuppressWarnings("CloneDoesntCallSuperClone")
    public Object clone() throws CloneNotSupportedException {
        return new CeylonSdkAdditionalData(myOptions.myJavaSdk, myOptions.myCeylonVersion);
    }

    public Sdk getJavaSdk() {
        return myOptions.myJavaSdk;
    }

    public void setJavaSdk(Sdk sdk) {
        myOptions.myJavaSdk = sdk;
    }

    public String getCeylonVersion() {
        return myOptions.myCeylonVersion;
    }

    @Override
    public void readExternal(Element element) throws InvalidDataException {
        DefaultJDOMExternalizer.readExternal(myOptions, element);
        String sdkName = element.getAttributeValue("sdk");
        for (Sdk sdk : ProjectJdkTable.getInstance().getAllJdks()) {
            if (sdk.getName().equals(sdkName)) {
                myOptions.myJavaSdk = sdk;
            }
        }
        myOptions.myCeylonVersion = element.getAttributeValue("ceylonVersion");
    }

    @Override
    public void writeExternal(Element element) throws WriteExternalException {
        DefaultJDOMExternalizer.writeExternal(myOptions, element);
        Sdk javaSdk = myOptions.myJavaSdk;
        if (javaSdk != null) {
            element.setAttribute("sdk", javaSdk.getName());
        }
        element.setAttribute("ceylonVersion", myOptions.myCeylonVersion);
    }

    public static class CeylonSdkOptions {
        @Nullable
        private Sdk myJavaSdk;

        @NotNull
        private String myCeylonVersion;
    }

    @Override
    public void checkValid(SdkModel sdkModel) throws ConfigurationException {
        // TODO check if internal JDK is set and > 1.7
    }
}
