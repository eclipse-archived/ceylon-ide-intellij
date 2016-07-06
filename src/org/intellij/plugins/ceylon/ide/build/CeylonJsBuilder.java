package org.intellij.plugins.ceylon.ide.build;

import com.intellij.openapi.compiler.CompileContext;
import com.intellij.openapi.compiler.CompileTask;
import com.intellij.openapi.compiler.CompilerMessageCategory;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.containers.MultiMap;
import com.redhat.ceylon.common.Backend;
import com.redhat.ceylon.compiler.js.JsCompiler;
import com.redhat.ceylon.compiler.js.util.Options;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Message;
import org.intellij.plugins.ceylon.ide.ceylonCode.ITypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProject;
import org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects;
import org.intellij.plugins.ceylon.ide.ceylonCode.settings.ceylonSettings_;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class CeylonJsBuilder implements CompileTask {

    @Override
    public boolean execute(CompileContext context) {
        if (ceylonSettings_.get_().getUseOutProcessBuild()) {
            return true;
        }
        IdeaCeylonProjects projects = context.getProject().getComponent(IdeaCeylonProjects.class);

        if (projects == null) {
            return true;
        }

        MultiMap<Module, VirtualFile> filesByModule = new MultiMap<>();

        for (VirtualFile vfile : context.getCompileScope().getFiles(null, true)) {
            Module module = context.getModuleByFile(vfile);
            filesByModule.putValue(module, vfile);
        }

        for (Module module : context.getCompileScope().getAffectedModules()) {
            IdeaCeylonProject project = (IdeaCeylonProject) projects.getProject(module);

            if (project != null && project.getCompileToJs()) {
                Collection<VirtualFile> files = filesByModule.get(module);
                List<File> jsFiles = new ArrayList<>();

                ITypeCheckerProvider provider = module.getComponent(ITypeCheckerProvider.class);
                if (provider == null) {
                    continue;
                }
                TypeChecker tc = provider.getTypeChecker();
                if (tc == null) {
                    continue;
                }

                for (VirtualFile file : files) {
                    if ((file.getExtension().equals("ceylon") || file.getExtension().equals("js"))
                            && isNative(file, context, module, tc, Backend.JavaScript)) {
                        jsFiles.add(new File(file.getPath()));
                    }
                }

                if (!compileJs(context, module, jsFiles, project, tc)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean compileJs(final CompileContext context, Module module, List<File> files,
                              IdeaCeylonProject project, TypeChecker tc) {

        context.addMessage(CompilerMessageCategory.INFORMATION,
                "Compiling module " + module.getName() + " to JS", null, -1, -1);

        List<File> sources = new ArrayList<>();
        for (VirtualFile source : ModuleRootManager.getInstance(module).getSourceRoots()) {
            sources.add(new File(source.getPath()));
        }

        Options options = new Options()
                .outWriter(new MessageWriter(context))
                .sourceDirs(sources)
                .systemRepo(project.getSystemRepository())
                .outRepo(context.getModuleOutputDirectory(module).getCanonicalPath())
                .optimize(true)
                .generateSourceArchive(true);

        JsCompiler compiler = new JsCompiler(tc, options).stopOnErrors(false);

        compiler.setSourceFiles(files);
        try {
            if (!compiler.generate()) {
                for (Message message : compiler.getErrors()) {
                    context.addMessage(CompilerMessageCategory.ERROR,
                            message.getMessage(), null, message.getLine(), -1);
                }

                return false;
            }
            context.addMessage(CompilerMessageCategory.INFORMATION,
                    "JS compilation succeeded", null, -1, -1);
        } catch (IOException e) {
            context.addMessage(CompilerMessageCategory.ERROR, e.getMessage(), null, -1, -1);
            return false;
        }

        return true;
    }

    private boolean isNative(VirtualFile vfile, CompileContext context, Module module, TypeChecker tc, Backend backend) {
        VirtualFile source = null;

        for (VirtualFile root : ModuleRootManager.getInstance(module).getSourceRoots()) {
            if (VfsUtil.isAncestor(root, vfile, true)) {
                source = root;
                break;
            }
        }

        if (source != null) {
            String relativePath = VfsUtil.getRelativePath(vfile, source);
            PhasedUnit pu = tc.getPhasedUnitFromRelativePath(relativePath);

            if (pu != null) {
                com.redhat.ceylon.model.typechecker.model.Module mod = pu.getPackage().getModule();

                return mod.getNativeBackends().none()
                        || mod.getNativeBackends().supports(backend);
            }
        }

        return false;
    }
}
