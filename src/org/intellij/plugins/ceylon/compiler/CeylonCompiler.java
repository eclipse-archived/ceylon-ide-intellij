package org.intellij.plugins.ceylon.compiler;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileScope;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.compiler.TranslatingCompiler;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ProjectRootManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.Chunk;
import org.intellij.plugins.ceylon.CeylonFileType;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
        // TODO replace with java com.redhat.ceylon.tools.CeylonTool with classpath from Ceylon SDK
        Module firstModule = moduleChunk.getNodes().iterator().next();

        List<String> params = new ArrayList<String>(Arrays.asList("cmd", "/c", "C:\\Users\\bjansen\\Downloads\\ceylon-0.4\\bin\\ceylon.bat",
                "compile",
                "--out", context.getModuleOutputDirectory(firstModule).getPath()));

        for (Module module : moduleChunk.getNodes()) {
            for (VirtualFile sourceRoot : context.getSourceRoots(module)) {
                params.add("--src");
                params.add(sourceRoot.getPath().replace("/", "\\"));
            }
        }

        // TODO check out how submodules work, need different src directories
        // TODO also compile files which don't belong to modules
        if (context.isRebuild() || context.isMake()) {
            for (VirtualFile file : files) {
                if (file.getName().equals("module.ceylon")) {
                    String pkgName = ProjectRootManager.getInstance(context.getProject()).getFileIndex().getPackageNameByDirectory(file.getParent());
                    params.add(pkgName);
                }
            }
        } else {
            for (VirtualFile file : files) {
                params.add(file.getPath());
            }
        }

        ProcessBuilder builder = new ProcessBuilder(params);

        try {
            Process process = builder.start();
            reportErrorsIfAny(context, process);
        } catch (IOException e) {
            context.addMessage(CompilerMessageCategory.ERROR, "Could not launch compiler", null, 0, 0);
            e.printStackTrace();
        }
    }

    private void reportErrorsIfAny(CompileContext context, Process process) throws IOException {
        InputStream in = process.getErrorStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        String line;

        while ((line = reader.readLine()) != null) {
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
        // TODO return false if no Ceylon SDK is configured
        return true;
    }
}
