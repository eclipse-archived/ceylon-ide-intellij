package org.intellij.plugins.ceylon.compiler;

import com.intellij.execution.configurations.CommandLineBuilder;
import com.intellij.execution.configurations.GeneralCommandLine;
import com.intellij.execution.configurations.ParametersList;
import com.intellij.execution.configurations.SimpleJavaParameters;
import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.projectRoots.JavaSdk;
import com.intellij.openapi.projectRoots.JavaSdkVersion;
import com.intellij.openapi.projectRoots.Sdk;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.OrderRootType;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import org.intellij.plugins.ceylon.CeylonFileType;
import org.intellij.plugins.ceylon.sdk.CeylonSdk;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CeylonCompiler implements TranslatingCompiler {

    public static final Pattern ERROR_PATTERN = Pattern.compile("(.*):(\\d+): error: (.*)");

    @Override
    public boolean isCompilableFile(VirtualFile file, CompileContext context) {
        return file.getFileType() == CeylonFileType.INSTANCE;
    }

    @Override
    public void compile(CompileContext context, Chunk<Module> moduleChunk, VirtualFile[] files, OutputSink sink) {
        Sdk sdk = ProjectRootManager.getInstance(context.getProject()).getProjectSdk();
        Sdk jdk = CeylonSdk.getInternalSdk(sdk);

        if (sdk == null || jdk == null) {
            return;
        }
        Module firstModule = moduleChunk.getNodes().iterator().next();
        SimpleJavaParameters parameters = new SimpleJavaParameters();
        parameters.setJdk(jdk);
        parameters.getVMParametersList().addProperty("ceylon.system.repo", sdk.getHomePath() + "/repo");
        parameters.setMainClass("com.redhat.ceylon.compiler.java.Main");

        try {
            ParametersList params = parameters.getProgramParametersList();
            params.add("-out", context.getModuleOutputDirectory(firstModule).getPath());

            for (Module module : moduleChunk.getNodes()) {
                for (VirtualFile sourceRoot : context.getSourceRoots(module)) {
                    params.add("-src", sourceRoot.getPath());
                }
            }

            for (VirtualFile file : files) {
                params.add(file.getPath());
            }

            VirtualFile[] roots = sdk.getSdkModificator().getRoots(OrderRootType.CLASSES);
            for (VirtualFile root : roots) {
                parameters.getClassPath().add(root);
            }

            GeneralCommandLine cline = CommandLineBuilder.createFromJavaParameters(parameters);
            context.addMessage(CompilerMessageCategory.WARNING, cline.toString(), null, 0, 0);
            Process process = cline.createProcess();
            reportErrorsIfAny(context, process);
        } catch (Exception e) {
            context.addMessage(CompilerMessageCategory.ERROR, "Could not launch compiler", null, 0, 0);
            e.printStackTrace();
        }
    }

    private void reportErrorsIfAny(CompileContext context, Process process) throws IOException {
        InputStream in = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        while ((line = reader.readLine()) != null) {
            System.out.println(line);
            Matcher matcher = ERROR_PATTERN.matcher(line);
            if (matcher.matches()) {
                reader.readLine();
                line = reader.readLine();
                int column = 0;
                if (line != null) {
                    column = line.indexOf('^') + 1;
                }
                context.addMessage(CompilerMessageCategory.ERROR, matcher.group(3),
                        "file://" + matcher.group(1).replace("\\", "/"), Integer.parseInt(matcher.group(2)), column);
            } else {
                context.addMessage(CompilerMessageCategory.INFORMATION, line, null, 0, 0);
            }
        }
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Ceylon compiler";
    }

    @Override
    public boolean validateConfiguration(CompileScope scope) {
        final Module[] modules = scope.getAffectedModules();

        for (final Module module : modules) {
            final Sdk sdk  = ModuleRootManager.getInstance(module).getSdk();
            if (sdk == null || !(sdk.getSdkType() instanceof CeylonSdk)) {
                Messages.showMessageDialog(module.getProject(), "No Ceylon SDK is configured in module " + module.getName(),
                        "Invalid Module Configuration", Messages.getErrorIcon());
                return false;
            }

            Sdk internalSdk = CeylonSdk.getInternalSdk(sdk);
            if (internalSdk == null) {
                Messages.showMessageDialog(module.getProject(), "No intenal JDK is configured in Ceylon SDK " + sdk.getName(),
                        "Invalid SDK Configuration", Messages.getErrorIcon());
                return false;
            } else {
                JavaSdkVersion version = JavaSdk.getInstance().getVersion(internalSdk);
                if (version == null || !version.isAtLeast(JavaSdkVersion.JDK_1_7)) {
                    Messages.showMessageDialog(module.getProject(), "Intenal JDK for Ceylon SDK " + sdk.getName() + " should be at least version 1.7",
                            "Invalid SDK Configuration", Messages.getErrorIcon());
                    return false;
                }
            }
        }

        return true;
    }
}
