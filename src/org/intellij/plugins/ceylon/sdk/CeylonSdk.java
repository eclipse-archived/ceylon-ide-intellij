package org.intellij.plugins.ceylon.sdk;

import com.intellij.openapi.projectRoots.*;
import com.intellij.openapi.projectRoots.impl.JavaDependentSdkType;
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
import java.util.ArrayList;
import java.util.List;

public class CeylonSdk extends JavaDependentSdkType implements JavaSdkType {

    private String version;

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
        return new File(path, "bin/ceylon").exists() && new File(path, "repo").isDirectory();
    }

    @Override
    public String suggestSdkName(String currentSdkName, String sdkHome) {
        String version = getVersionString(sdkHome);
        return version == null ? "Ceylon SDK" : "Ceylon " + version;
    }

    @Nullable
    @Override
    public String getVersionString(String sdkHome) {
        // TODO that's not nice at all :(
        return version;
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

        version = additionalData.getCeylonVersion();

        return additionalData;
    }

    @Override
    public void saveAdditionalData(@NotNull SdkAdditionalData additionalData, @NotNull Element additional) {
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
    public static Sdk getInternalSdk(@Nullable Sdk sdk) {
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
        version = chooseCeylonVersion(sdk);

        if (version == null) {
            Messages.showErrorDialog("Could not find any suitable Ceylon language version", "Cannot Set Up SDK");
            return false;
        }

        String jdkVersion = chooseJdkVersion(sdkModel);

        if (jdkVersion == null) {
            Messages.showErrorDialog("Could not find any suitable JDK", "Cannot Set Up SDK");
            return false;
        }

        SdkModificator sdkModificator = sdk.getSdkModificator();

        sdkModificator.setSdkAdditionalData(new CeylonSdkAdditionalData(sdkModel.findSdk(jdkVersion), version));

        addCeylonLibraries(sdk, sdkModificator);

        sdkModificator.commitChanges();

        return true;
    }

    /**
     * Adds vital ceylon jar/car files to the classpath.
     * @param sdk the ceylon SDK
     * @param sdkModificator the SDK modificator
     */
    private void addCeylonLibraries(Sdk sdk, SdkModificator sdkModificator) {
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
    }

    /**
     * Asks the user to choose which Java SDK to use for this Ceylon SDK.
     * @param sdkModel the ceylon SDK
     * @return the Java SDK name to use
     */
    private String chooseJdkVersion(SdkModel sdkModel) {
        final List<String> javaSdks = new ArrayList<String>();
        final Sdk[] sdks = sdkModel.getSdks();
        for (Sdk jdk : sdks) {
            if (isValidInternalJdk(jdk)) {
                javaSdks.add(jdk.getName());
            }
        }
        final int choice = Messages.showChooseDialog("Select Java SDK to be used as the internal platform", "Select Internal Java Platform",
                ArrayUtil.toStringArray(javaSdks), javaSdks.get(0), Messages.getQuestionIcon());

        if (choice == -1) {
            return null;
        }

        return javaSdks.get(choice);
    }

    /**
     * Asks the user to choose a  Ceylon language version to use for this SDK (from repo/ceylon/language/xxx).
     *
     * @param sdk the ceylon SDK
     * @return the Ceylon language version to use
     */
    private String chooseCeylonVersion(Sdk sdk) {
        if (sdk.getHomeDirectory() == null) {
            return null;
        }
        VirtualFile languageDirectory = sdk.getHomeDirectory().findFileByRelativePath("repo/ceylon/language");

        if (languageDirectory == null || !languageDirectory.exists()) {
            return null;
        }

        List<String> versions = new ArrayList<String>();

        for (VirtualFile child : languageDirectory.getChildren()) {
            if (child.isDirectory()) {
                versions.add(child.getName());
            }
        }

        final int choice = Messages.showChooseDialog("Select a Ceylon language version to use", "Select Ceylon Version",
                ArrayUtil.toStringArray(versions), versions.get(0), Messages.getQuestionIcon());

        return versions.get(choice);
    }

    /**
     * Adds a jar/car file from the Ceylon repository to the SDK's classpath.
     * @param sdk the ceylon SDK being configured
     * @param homeDirectory the SDK's base directory
     * @param sdkModificator the SDK modificator
     * @param jarName the jar to add to the classpath
     * @param extension the file extension (car or jar)
     */
    private void addJarFromRepo(Sdk sdk, @NotNull VirtualFile homeDirectory, SdkModificator sdkModificator, String jarName, String extension) {
        VirtualFile file = homeDirectory.findFileByRelativePath(
                String.format("repo/%s/%s/%s-%s.%s", jarName.replace('.', '/'), version, jarName, sdk.getVersionString(), extension));

        if (file != null) {
            VirtualFile jar = JarFileSystem.getInstance().findFileByPath(file.getPath() + JarFileSystem.JAR_SEPARATOR);
            sdkModificator.addRoot(jar, OrderRootType.CLASSES);
        }
    }

    /**
     * Checks if the given SDK is suitable to be the Java SDK used by the Ceylon SDK (JDK 7+).
     * @param jdk the SDK to test
     * @return true if {@code sdk} is suitable
     */
    private boolean isValidInternalJdk(Sdk jdk) {
        return jdk.getSdkType() instanceof JavaSdkType && JavaSdk.getInstance().isOfVersionOrHigher(jdk, JavaSdkVersion.JDK_1_7);
    }
}
