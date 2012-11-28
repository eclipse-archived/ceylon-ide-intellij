package org.intellij.plugins.ceylon.sdk;

import com.google.common.io.Files;
import com.google.common.io.LineProcessor;
import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.vfs.JarFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.ArrayUtil;
import org.jdom.Element;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

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
    public boolean setupSdkPaths(final Sdk sdk, final SdkModel sdkModel) {
        final List<String> javaSdks = new ArrayList<String>();
        final Sdk[] sdks = sdkModel.getSdks();
        for (Sdk jdk : sdks) {
            if (isValidInternalJdk(jdk)) {
                javaSdks.add(jdk.getName());
            }
        }
        final int choice = Messages.showChooseDialog("Select Java SDK to be used as the internal platform", "Select Internal Java Platform",
                ArrayUtil.toStringArray(javaSdks), javaSdks.get(0), Messages.getQuestionIcon());

        SdkModificator sdkModificator = sdk.getSdkModificator();

        if (choice == -1) {
            return false;
        }

        sdkModificator.setSdkAdditionalData(new CeylonSdkAdditionalData(sdkModel.findSdk(javaSdks.get(choice))));

        VirtualFile homeDirectory = sdk.getHomeDirectory();

        if (homeDirectory != null) {
            VirtualFile libDir = homeDirectory.findChild("lib");

            if (libDir != null) {
                for (VirtualFile file : libDir.getChildren()) {
                    if ("jar".equals(file.getExtension())) {
                        VirtualFile jar = JarFileSystem.getInstance().findFileByPath(file.getPath() + JarFileSystem.JAR_SEPARATOR);
                        sdkModificator.addRoot(jar, OrderRootType.CLASSES);
                    }
                }
            }

            addJarFromRepo(sdk, homeDirectory, sdkModificator, "ceylon.language", "car");
            addJarFromRepo(sdk, homeDirectory, sdkModificator, "com.redhat.ceylon.common", "jar");
            addJarFromRepo(sdk, homeDirectory, sdkModificator, "com.redhat.ceylon.compiler.java", "jar");
            addJarFromRepo(sdk, homeDirectory, sdkModificator, "com.redhat.ceylon.module-resolver", "jar");
            addJarFromRepo(sdk, homeDirectory, sdkModificator, "com.redhat.ceylon.typechecker", "jar");
        }

        sdkModificator.commitChanges();

        return true;
    }

    private void addJarFromRepo(Sdk sdk, @NotNull VirtualFile homeDirectory, SdkModificator sdkModificator, String jarName, String extension) {
        VirtualFile file = homeDirectory.findFileByRelativePath(
                String.format("repo/%s/%s/%s-%s.%s", jarName.replace('.', '/'), sdk.getVersionString(), jarName, sdk.getVersionString(), extension));

        if (file != null) {
            VirtualFile jar = JarFileSystem.getInstance().findFileByPath(file.getPath() + JarFileSystem.JAR_SEPARATOR);
            sdkModificator.addRoot(jar, OrderRootType.CLASSES);
        }
    }

    private boolean isValidInternalJdk(Sdk jdk) {
        return jdk.getSdkType() instanceof JavaSdkType && JavaSdk.getInstance().isOfVersionOrHigher(jdk, JavaSdkVersion.JDK_1_7);
    }
}
