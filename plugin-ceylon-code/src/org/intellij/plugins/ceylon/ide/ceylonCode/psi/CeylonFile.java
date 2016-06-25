package org.intellij.plugins.ceylon.ide.ceylonCode.psi;

import java.util.ArrayList;
import java.util.List;

import org.antlr.runtime.CommonToken;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.jetbrains.annotations.NotNull;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.util.Computable;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassOwner;
import com.intellij.psi.text.BlockSupport;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.compiler.typechecker.tree.Tree.CompilationUnit;
import com.redhat.ceylon.ide.common.model.BaseCeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProject;
import com.redhat.ceylon.ide.common.model.CeylonProjects;
import com.redhat.ceylon.ide.common.model.IdeModule;
import com.redhat.ceylon.ide.common.platform.CommonDocument;
import com.redhat.ceylon.ide.common.platform.Status;
import com.redhat.ceylon.ide.common.platform.platformUtils_;
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult;
import com.redhat.ceylon.ide.common.typechecker.LocalAnalysisResult$impl;
import com.redhat.ceylon.ide.common.typechecker.ProjectPhasedUnit;
import com.redhat.ceylon.ide.common.vfs.BaseFileVirtualFile;
import com.redhat.ceylon.model.typechecker.model.Cancellable;

import static com.intellij.psi.util.PsiTreeUtil.getChildrenOfType;

public class CeylonFile extends PsiFileBase implements PsiClassOwner {
    
    public CeylonFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, CeylonLanguage.INSTANCE);
    }

    private CeylonPsi.CompilationUnitPsi getCompilationUnitPsi() {
        return PsiTreeUtil.findChildOfType(this, CeylonPsi.CompilationUnitPsi.class);
    }

    public Tree.CompilationUnit getCompilationUnit() {
        CeylonPsi.CompilationUnitPsi cu = getCompilationUnitPsi();
        if (cu != null) {
            return cu.getCeylonNode();
        }
        return null;
    }

    private VirtualFile realVirtualFile() {
        VirtualFile virtualFile;
        if (getOriginalFile() != null) {
            virtualFile = getOriginalFile().getVirtualFile();
        } else {
            virtualFile = getVirtualFile();
        }
        return virtualFile;
    }
    
    public CeylonLocalAnalyzer getLocalAnalyzer() {
        CeylonLocalAnalyzerManager localAnalyzerManager = getProject().getComponent(CeylonLocalAnalyzerManager.class);
        return localAnalyzerManager.get(realVirtualFile());
    }
    
    @SuppressWarnings("unchecked")
    ProjectPhasedUnit<Module, VirtualFile, VirtualFile, VirtualFile> retrieveCorrespondingPhasedUnit() {
        Module module = ModuleUtil.findModuleForPsiElement(this);
        CeylonProjects<Module, VirtualFile, VirtualFile, VirtualFile> ceylonProjects = getProject().getComponent(org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects.class);
        CeylonProject<Module, VirtualFile, VirtualFile, VirtualFile> ceylonProject = ceylonProjects.getProject(module);
        if (ceylonProject != null) {
            BaseFileVirtualFile ceylonVirtualFile = ceylonProject.projectFileFromNative(realVirtualFile());
            if (ceylonVirtualFile != null) {
                return ceylonProject.getParsedUnit(ceylonVirtualFile);
            }
        }
        return null;
    }
    
    public TypeChecker getTypechecker() {
        if (isInSourceArchive_.isInSourceArchive(realVirtualFile())) {
            org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects ceylonProjects = getProject().getComponent(org.intellij.plugins.ceylon.ide.ceylonCode.model.IdeaCeylonProjects.class);
            IdeModule<Module, VirtualFile, VirtualFile, VirtualFile> mod = ceylonProjects.findModuleForExternalPhasedUnit(realVirtualFile());
            if (mod != null) {
                BaseCeylonProject ceylonProject = mod.getCeylonProject();
                if (ceylonProject != null) {
                    return ceylonProject.getTypechecker();
                }
            }
            return null;
        }
        
        ProjectPhasedUnit<?, ?, ?, ?> projectPhasedUnit = retrieveCorrespondingPhasedUnit();
        if (projectPhasedUnit != null) {
            return projectPhasedUnit.getTypeChecker();
        }
        
        LocalAnalysisResult localAnalysisResult = getLocalAnalysisResult();
        if (localAnalysisResult != null) {
            return localAnalysisResult.getTypeChecker();
        }
        return null;
    }
    
    public LocalAnalysisResult getLocalAnalysisResult() {
        final Tree.CompilationUnit attachedCompilationUnit = 
                ApplicationManager.getApplication().runReadAction(new Computable<Tree.CompilationUnit>() {
                    @Override
                    public Tree.CompilationUnit compute() {
                        return CeylonFile.this.getCompilationUnit();
                    }
                });
        
        CeylonLocalAnalyzer localAnalyzer = getLocalAnalyzer();
        if (localAnalyzer != null) {
            LocalAnalysisResult result = localAnalyzer.getResult();
            if (result != null) {
                if (result.getParsedRootNode() == attachedCompilationUnit) {
                    return result;
                } else {
                    platformUtils_.get_().log(Status.getStatus$_WARNING(), "LocalAnalysisResult.parsedRootNode !== ceylonFile.compilationUnit ");
                }
            }
        }
        if (! isInSourceArchive_.isInSourceArchive(realVirtualFile())) {
            final ProjectPhasedUnit<Module, VirtualFile, VirtualFile, VirtualFile> projectPhasedUnit = retrieveCorrespondingPhasedUnit();
            if (projectPhasedUnit != null 
                    && projectPhasedUnit.isFullyTyped()) {
                if (projectPhasedUnit.getCompilationUnit() == attachedCompilationUnit) {
                    return new LocalAnalysisResult() {
                        public boolean getUpToDate() {
                            return true;
                        }
                        @Override
                        public CompilationUnit getTypecheckedRootNode() {
                            return attachedCompilationUnit;
                        }
                        @Override
                        public TypeChecker getTypeChecker() {
                            return projectPhasedUnit.getTypeChecker();
                        }
                        @Override
                        public List<CommonToken> getTokens() {
                            return projectPhasedUnit.getTokens();
                        }
                        @Override
                        public CompilationUnit getParsedRootNode() {
                            return attachedCompilationUnit;
                        }
                        @Override
                        public PhasedUnit getLastPhasedUnit() {
                            return projectPhasedUnit;
                        }
                        @Override
                        public CompilationUnit getLastCompilationUnit() {
                            return attachedCompilationUnit;
                        }
                        @Override
                        public CommonDocument getCommonDocument() {
                            return null;
                        }
                        @Override
                        public BaseCeylonProject getCeylonProject() {
                            return projectPhasedUnit.getCeylonProject();
                        }
                        LocalAnalysisResult$impl $impl = new LocalAnalysisResult$impl(this);
                        @Override
                        public LocalAnalysisResult$impl $com$redhat$ceylon$ide$common$typechecker$LocalAnalysisResult$impl() {
                            return $impl;
                        }
                    };
                } else {
                    platformUtils_.get_().log(Status.getStatus$_WARNING(), "ProjectPhasedUnit.compilationUnit !== ceylonFile.compilationUnit ");
                }
            }
        }
                
        platformUtils_.get_().log(Status.getStatus$_DEBUG(), "localAnalysisResult requested, but was null");
        return null;
    }
    
    @NotNull
    @Override
    public FileType getFileType() {
        return CeylonFileType.INSTANCE;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getUserData(@NotNull Key<T> key) {
        if (BlockSupport.TREE_DEPTH_LIMIT_EXCEEDED.equals(key)) {
            // This prevents ReparsedSuccessfullyException from ever being thrown so we can bind AST nodes to Ceylon Spec nodes in CeylonIdeaParser.
            //noinspection unchecked
            return (T) Boolean.TRUE;
        }
        return super.getUserData(key);
    }

    public PhasedUnit getUpToDatePhasedUnit() {
        LocalAnalysisResult analysisResult = getLocalAnalysisResult();
        if (analysisResult != null) {
            if (analysisResult.getTypecheckedRootNode() != null) {
                return analysisResult.getLastPhasedUnit();
            }
        }
        return null;
    }
    
    private PhasedUnit ensureTypechecked(Cancellable cancellable, int timeoutSeconds) {
        CeylonLocalAnalyzer localAnalyzer = getLocalAnalyzer();
        if (localAnalyzer != null) {
            return localAnalyzer.ensureTypechecked(cancellable, timeoutSeconds);
        }
        return null;
    }

    public PhasedUnit ensureTypechecked() {
        return ensureTypechecked(null);
    }
    
    public PhasedUnit ensureTypechecked(Cancellable cancellable) {
        return ensureTypechecked(cancellable, 5);
    }

    public PhasedUnit forceReparse() {
        putUserData(IdeaCeylonParser.FORCED_CU_KEY, null);
        onContentReload();
        getNode().getLastChildNode();
        return ensureTypechecked();
    }

    @Override
    public void subtreeChanged() {
        super.subtreeChanged();
    }

    @NotNull
    @Override
    public PsiClass[] getClasses() {
        List<PsiClass> classes = new ArrayList<>();

        CeylonPsi.DeclarationPsi[] decls =
                getChildrenOfType(getCompilationUnitPsi(), CeylonPsi.DeclarationPsi.class);
        if (decls != null) {
            for (CeylonPsi.DeclarationPsi decl : decls) {
                classes.add(new NavigationPsiClass(decl));
            }
        }

        return classes.toArray(new PsiClass[classes.size()]);
    }

    @Override
    public String getPackageName() {
        return "";
    }

    @Override
    public void setPackageName(String packageName) throws IncorrectOperationException {
    }
}
