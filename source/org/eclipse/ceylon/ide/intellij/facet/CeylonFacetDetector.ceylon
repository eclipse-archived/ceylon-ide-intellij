import com.intellij.framework.detection {
    FacetBasedFrameworkDetector,
    FileContentPattern
}
import com.intellij.ide.projectView.actions {
    MarkRootActionBase
}
import com.intellij.openapi.fileEditor {
    FileEditorManager
}
import com.intellij.openapi.options {
    ConfigurationException
}
import com.intellij.openapi.roots {
    ModifiableRootModel
}
import com.intellij.openapi.roots.ui.configuration {
    ModulesConfigurator
}
import com.intellij.openapi.vfs {
    VfsUtil
}
import org.eclipse.ceylon.common.config {
    CeylonConfigFinder,
    DefaultToolOptions
}
import java.io {
    File,
    IOException
}
import org.eclipse.ceylon.ide.intellij.lang {
    CeylonFileType
}
import org.eclipse.ceylon.ide.intellij.model {
    IdeaCeylonProject,
    CeylonProjectManager,
    getCeylonProjects
}
import org.eclipse.ceylon.ide.intellij.project {
    CeylonModuleBuilder {
        setCompilerOutput
    }
}
import org.eclipse.ceylon.ide.intellij.psi {
    CeylonLocalAnalyzerManager
}
import org.eclipse.ceylon.ide.intellij.settings {
    ceylonSettings
}
import org.jetbrains.jps.model.java {
    JavaResourceRootType
}

"Detects if a missing Ceylon facet could be added to the current module."
shared class CeylonFacetDetector()
        extends FacetBasedFrameworkDetector<CeylonFacet,CeylonFacetConfiguration>("ceylon") {

    facetType => CeylonFacet.facetType;
    fileType => CeylonFileType.instance;
    createSuitableFilePattern() => FileContentPattern.fileContent();

    shared actual void setupFacet(CeylonFacet facet, ModifiableRootModel model) {
        if (exists ceylonModel = getCeylonProjects(facet.\imodule.project),
            is IdeaCeylonProject project = ceylonModel.getProject(facet.\imodule)) {

            value virtualFile = model.sourceRoots.size>0
                then model.sourceRoots.get(0)
                else project.moduleRoot;

            try {
                if (exists config = CeylonConfigFinder.loadLocalConfig(File(virtualFile.canonicalPath))) {
                    value outputRepo = DefaultToolOptions.getCompilerOutputRepo(config) else "./modules";
                    value outputDirectory = File(project.rootDirectory, outputRepo);

                    setCompilerOutput(model, outputDirectory);

                    if (exists resources = config.getOptionValues(DefaultToolOptions.compilerResource)) {
                        for (res in resources) {
                            variable value resourceFile = File(res.string);
                            if (!resourceFile.absolute) {
                                resourceFile = File(project.rootDirectory, res.string);
                            }
                            addResourceRoot(model, resourceFile);
                        }
                    } else {
                        addResourceRoot(model, File(project.rootDirectory, "resource"));
                    }
                }
            }
            catch (IOException|ConfigurationException e) {
                e.printStackTrace();
            }

            value defaultVm = ceylonSettings.defaultTargetVm;
            project.ideConfiguration.compileToJs = defaultVm != "js";
            project.ideConfiguration.compileToJvm = defaultVm != "jvm";
            project.configuration.projectOffline = false;

            ModulesConfigurator.showFacetSettingsDialog(facet, CeylonFacetConfiguration.compilationTab);

            CeylonProjectManager.forModule(facet.\imodule).moduleAdded();

            if (exists analyzerManager = model.project.getComponent(`CeylonLocalAnalyzerManager`)) {
                value fem = FileEditorManager.getInstance(model.project);
                analyzerManager.ceylonFacetAdded(fem);
            }
        }
    }

    void addResourceRoot(ModifiableRootModel model, File resourceDir) {
        if (resourceDir.\iexists(),
            exists vFile = VfsUtil.findFileByIoFile(resourceDir, false),
            exists contentEntry = MarkRootActionBase.findContentEntry(model, vFile)) {

            contentEntry.addSourceFolder(vFile, JavaResourceRootType.resource);
        }
    }
}
