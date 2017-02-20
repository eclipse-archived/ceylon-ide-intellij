import com.intellij.facet {
    FacetManager
}
import com.intellij.ide.util.projectWizard {
    JavaModuleBuilder,
    ModuleBuilderListener,
    WizardContext
}
import com.intellij.openapi {
    Disposable
}
import com.intellij.openapi.\imodule {
    Module
}
import com.intellij.openapi.options {
    ConfigurationException
}
import com.intellij.openapi.roots {
    CompilerModuleExtension,
    ModifiableRootModel
}
import com.intellij.openapi.util {
    Pair
}
import com.intellij.openapi.util.io {
    FileUtil
}
import com.intellij.openapi.vfs {
    LocalFileSystem
}

import java.io {
    File,
    IOException
}
import java.lang {
    JString=String
}
import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.facet {
    CeylonFacet
}
import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject,
    CeylonProjectManager,
    getCeylonProjects
}
import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}
import org.intellij.plugins.ceylon.ide.startup {
    CeylonIdePlugin {
        embeddedCeylonDist
    }
}
import org.jetbrains.jps.model.java {
    JavaResourceRootType,
    JavaSourceRootType
}

shared class CeylonModuleBuilder extends JavaModuleBuilder {

    shared static void setCompilerOutput(ModifiableRootModel rootModel, File outDirectory) {
        value compiler = rootModel.getModuleExtension(`CompilerModuleExtension`);
        try {
            compiler.setCompilerOutputPath(outDirectory.canonicalPath);
            compiler.setCompilerOutputPathForTests(outDirectory.canonicalPath);
        }
        catch (IOException e) {
            throw ConfigurationException(e.message);
        }
        compiler.inheritCompilerOutputPath(false);
        compiler.commit();
    }

    value mySourcePaths = ArrayList<Pair<JString,JString>>();

    variable CeylonPageOne? pageOne = null;
    variable CeylonPageTwo? pageTwo = null;
    variable PageZero? pageZero = null;

    shared void persistConfiguration(Module mod) {
        assert(exists projects = getCeylonProjects(mod.project));
        projects.addProject(mod);
        assert (is IdeaCeylonProject ceylonProject = projects.getProject(mod));
        if (pageZero?.isCreateBootstrapFiles()?.booleanValue() else false) {
            ceylonProject.createBootstrapFiles(embeddedCeylonDist, pageZero?.version else "");
        }
        pageOne?.apply(ceylonProject);
        pageTwo?.apply(ceylonProject);

        ceylonProject.configuration.projectSourceDirectories = {ceylonSettings.defaultSourceFolder};
        ceylonProject.configuration.projectResourceDirectories = {ceylonSettings.defaultResourceFolder};
        ceylonProject.configuration.save();
        ceylonProject.ideConfiguration.save();

        FacetManager.getInstance(mod)
            .addFacet(CeylonFacet.facetType, CeylonFacet.facetType.presentableName, null)
            .configuration
            .setModule(mod);
    }

    shared new () extends JavaModuleBuilder() {
        addListener(object satisfies ModuleBuilderListener {

            shared actual void moduleCreated(Module mod) {
                persistConfiguration(mod);
                CeylonProjectManager.forModule(mod).moduleAdded();
            }
        });
    }

    getCustomOptionsStep(WizardContext context, Disposable parentDisposable)
            => PageZeroWizardStep(this);

    shared actual void setupRootModel(ModifiableRootModel rootModel) {
        rootModel.inheritSdk();
        value contentEntry = doAddContentEntry(rootModel);
        if (exists contentEntry) {
            for (sourcePath in sourcePaths) {
                value first = sourcePath.first;

                File(first.string).mkdirs();

                value sourceRoot = LocalFileSystem.instance.refreshAndFindFileByPath(FileUtil.toSystemIndependentName(first.string));
                if (exists sourceRoot) {
                    value elType = if ("resource".equals(sourcePath.second)) then JavaResourceRootType.resource else JavaSourceRootType.source;
                    contentEntry.addSourceFolder(sourceRoot, elType);
                }
            }
            value outPath = (contentEntryPath else "") + File.separator + (pageTwo?.outputDirectory else "");
            value outDirectory = File(FileUtil.toSystemIndependentName(outPath));
            if (!outDirectory.\iexists()) {
                outDirectory.mkdir();
            }
            value virtualFile = LocalFileSystem.instance.refreshAndFindFileByIoFile(outDirectory);
            if (exists virtualFile) {
                contentEntry.addExcludeFolder(virtualFile);
            }
            setCompilerOutput(rootModel, outDirectory);
        }
    }

    moduleType => CeylonModuleType.instance;

    shared actual List<Pair<JString,JString>> sourcePaths {
        if (mySourcePaths.empty) {
            value paths = ArrayList<Pair<JString,JString>>();
            value source = ceylonSettings.defaultSourceFolder;
            value resource = ceylonSettings.defaultResourceFolder;
            value sourcePath = (contentEntryPath else "") + File.separator + source;
            value resourcePath = (contentEntryPath else "") + File.separator + resource;

            File(sourcePath).mkdirs();
            File(resourcePath).mkdirs();

            paths.add(Pair.create(JString(sourcePath), JString("source")));
            paths.add(Pair.create(JString(resourcePath), JString("resource")));

            return paths;
        }
        return mySourcePaths;
    }

    assign sourcePaths {
        // TODO mySourcePaths = ArrayList(sourcePaths);
    }

    addSourcePath(Pair<JString,JString> sourcePathInfo)
            => mySourcePaths.add(sourcePathInfo);

    shared void setPageOne(CeylonPageOne pageOne)
            => this.pageOne = pageOne;

    shared void setPageTwo(CeylonPageTwo pageTwo)
            => this.pageTwo = pageTwo;

    shared void setPageZero(PageZero pageZero)
            => this.pageZero = pageZero;

    // unfortunately we have to refine those because setters in AbstractModuleBuilder
    // were transformed to properties in ModuleBuilder :(

    setContentEntryPath(String? moduleRootPath)
            => super.contentEntryPath = moduleRootPath;

    setModuleFilePath(String? path)
            => super.moduleFilePath = path;

    setName(String? name)
            => super.name = name;

}
