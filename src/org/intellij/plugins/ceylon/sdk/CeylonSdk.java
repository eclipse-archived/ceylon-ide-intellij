package org.intellij.plugins.ceylon.sdk;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;

public class CeylonSdk extends SdkType implements JavaSdkType {

    public CeylonSdk() {
        super("Ceylon SDK");
    }

    @Nullable
    @Override
    public String suggestHomePath() {
        return null;
    }

    @Override
    public boolean isValidSdkHome(String path) {
        return new File(path, "bin/ceylon").exists();
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        String version = getVersionString(sdkHome);
        return version == null ? "Ceylon SDK" : "Ceylon " + version;
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        File cpSh = new File(sdkHome, "bin/ceylon-cp.sh");

        if (cpSh.exists()) {
            try {
                return Files.readLines(cpSh, Charset.defaultCharset(), new LineProcessor<String>() {
                    private String version;

                    @Override
                    public boolean processLine(String s) throws IOException {
                        if (s.startsWith("CEYLON_VERSION=")) {
                            version = s.substring(s.indexOf('=') + 1);
                            return false;
                        }
                        return true;
                    }

                    @Override
                    public String getResult() {
                        return version == null ? null : version;
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    @Nullable
    @Override
    public AdditionalDataConfigurable createAdditionalDataConfigurable(SdkModel sdkModel, SdkModificator sdkModificator) {
        return new CeylonSdkAdditionalDataConfigurable(sdkModel, sdkModificator);
    }

    @Override
    public String getPresentableName() {
        return "Ceylon SDK";
    }

    @Nullable
    @Override
    public SdkAdditionalData loadAdditionalData(Element additional) {
        CeylonSdkAdditionalData additionalData = new CeylonSdkAdditionalData();
        try {
            additionalData.readExternal(additional);
        } catch (InvalidDataException e) {
            e.printStackTrace();
        }
        return additionalData;
    }

    @Override
    public void saveAdditionalData(SdkAdditionalData additionalData, Element additional) {
        if (additionalData instanceof CeylonSdkAdditionalData) {
            try {
                ((CeylonSdkAdditionalData) additionalData).writeExternal(additional);
            } catch (WriteExternalException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public String getBinPath(Sdk sdk) {
        Sdk internalSdk = getInternalSdk(sdk);
        return internalSdk == null ? null : JavaSdk.getInstance().getBinPath(internalSdk);
    }

    @Override
    public String getToolsPath(Sdk sdk) {
        Sdk internalSdk = getInternalSdk(sdk);
        return internalSdk == null ? null : JavaSdk.getInstance().getToolsPath(internalSdk);
    }

    @Override
    public String getVMExecutablePath(Sdk sdk) {
        Sdk internalSdk = getInternalSdk(sdk);
        return internalSdk == null ? null : JavaSdk.getInstance().getVMExecutablePath(internalSdk);
    }

    @Nullable
    public static Sdk getInternalSdk(Sdk sdk) {
        if (sdk == null) {
            return null;
        }
        SdkAdditionalData additionalData = sdk.getSdkAdditionalData();

        if (additionalData instanceof CeylonSdkAdditionalData) {
            return ((CeylonSdkAdditionalData) additionalData).getJavaSdk();
        }
        return null;
    }

    @Override
    public void setupSdkPaths(Sdk sdk) {
        File home = new File(sdk.getHomePath());
        SdkModificator sdkModificator = sdk.getSdkModificator();

        // TODO add libraries used in compilation
//        File libDir = new File(home, "lib");
//        sdkModificator.addRoot(OrderRootType.C);
    }
}
