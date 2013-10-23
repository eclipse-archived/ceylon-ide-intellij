package org.intellij.plugins.ceylon.sdk;

import com.intellij.openapi.projectRoots.ProjectJdkTable;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.projectRoots.SdkAdditionalData;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;

public class CeylonSdkAdditionalData implements SdkAdditionalData {

    private CeylonSdkOptions myOptions = new CeylonSdkOptions();

    public CeylonSdkAdditionalData() {

    }

    public CeylonSdkAdditionalData(@NotNull Sdk sdk, @NotNull String ceylonVersion) {
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

    public void readExternal(Element element) throws InvalidDataException {
        String sdkName = element.getAttributeValue("sdk");

        for (Sdk sdk : ProjectJdkTable.getInstance().getAllJdks()) {
            if (sdk.getName().equals(sdkName)) {
                myOptions.myJavaSdk = sdk;
            }
        }
        myOptions.myCeylonVersion = element.getAttributeValue("ceylonVersion");
    }

    public void writeExternal(Element element) throws WriteExternalException {
        element.setAttribute("sdk", myOptions.myJavaSdk.getName());
        element.setAttribute("ceylonVersion", myOptions.myCeylonVersion);
    }

    public static class CeylonSdkOptions {
        @NotNull
        private Sdk myJavaSdk;

        @NotNull
        private String myCeylonVersion;
    }
}
