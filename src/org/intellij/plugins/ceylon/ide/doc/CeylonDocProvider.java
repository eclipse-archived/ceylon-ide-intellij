package org.intellij.plugins.ceylon.ide.doc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiElementBase;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.doc.docGenerator_;
import org.intellij.plugins.ceylon.ide.psi.CeylonFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * @author Matija Mazi <br/>
 */
public class CeylonDocProvider extends AbstractDocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return generateDoc(element, originalElement);
    }

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        try {
            if (element instanceof DummyPsiElement) {
                Referenceable referenceable = ((DummyPsiElement) element).referenceable;
                return docGenerator_.get_().getDocumentationText(referenceable, null).value;
            }
            return docGenerator_.get_().getDocumentation(((CeylonFile) element.getContainingFile()).getCompilationUnit(), element.getTextOffset());
        } catch (ceylon.language.AssertionError | Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        return contextElement;
    }

    // FIXME refactor with com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel(String, TypeChecker)
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        String[] bits = link.split(":");
        String moduleNameAndVersion = bits[1];
        int loc = moduleNameAndVersion.indexOf('/');
        String moduleName = moduleNameAndVersion.substring(0, loc);
        String moduleVersion = moduleNameAndVersion.substring(loc + 1);

        TypeChecker tc = ModuleUtil.findModuleForFile(context.getContainingFile().getVirtualFile(), context.getProject()).getComponent(TypeCheckerProvider.class).getTypeChecker();

        Module theModule = null;

        for (Module module : tc.getContext().getModules().getListOfModules()) {
            if (module.getNameAsString().equals(moduleName) && module.getVersion().equals(moduleVersion)) {
                theModule = module;
            }
        }

        if (theModule != null) {
            Referenceable target = theModule.getPackage(bits[2]);

            for (int i = 3; i < bits.length; i++) {
                Scope scope;
                if (target instanceof Scope) {
                    scope = (Scope) target;
                } else if (target instanceof TypedDeclaration) {
                    TypedDeclaration td = (TypedDeclaration) target;
                    scope = td.getType().getDeclaration();
                } else {
                    return null;
                }
                if (scope instanceof Value) {
                    Value v = (Value) scope;
                    TypeDeclaration val = v.getTypeDeclaration();
                    if (val.isAnonymous()) {
                        scope = val;
                    }
                }
                target = scope.getDirectMember(bits[i], null, false);
            }

            if (target != null) {
                return new DummyPsiElement(target, context.getContainingFile());
            }
        }

        return null;
    }

    /**
     * A trick to redirect a click on a link to generateDoc() without actually knowing which
     * PsiElement represents the target Referenceable.
     */
    private static class DummyPsiElement extends PsiElementBase {

        private Referenceable referenceable;
        private PsiFile containingFile;

        public DummyPsiElement(Referenceable referenceable, PsiFile containingFile) {
            this.referenceable = referenceable;
            this.containingFile = containingFile;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @NotNull
        @Override
        public Project getProject() {
            return containingFile.getProject();
        }

        @NotNull
        @Override
        public Language getLanguage() {
            return CeylonLanguage.INSTANCE;
        }

        @NotNull
        @Override
        public PsiElement[] getChildren() {
            return PsiElement.EMPTY_ARRAY;
        }

        @Override
        public PsiElement getParent() {
            return containingFile;
        }

        @Override
        public TextRange getTextRange() {
            return null;
        }

        @Override
        public int getStartOffsetInParent() {
            return 0;
        }

        @Override
        public int getTextLength() {
            return 0;
        }

        @Nullable
        @Override
        public PsiElement findElementAt(int offset) {
            return null;
        }

        @Override
        public int getTextOffset() {
            return 0;
        }

        @Override
        public String getText() {
            return null;
        }

        @NotNull
        @Override
        public char[] textToCharArray() {
            return new char[0];
        }

        @Override
        public ASTNode getNode() {
            return null;
        }
    }
}

