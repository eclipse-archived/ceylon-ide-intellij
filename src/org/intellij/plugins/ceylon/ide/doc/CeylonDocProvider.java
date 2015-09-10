package org.intellij.plugins.ceylon.ide.doc;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.impl.PsiElementBase;
import com.intellij.psi.tree.IElementType;
import com.redhat.ceylon.compiler.typechecker.TypeChecker;
import com.redhat.ceylon.compiler.typechecker.context.PhasedUnit;
import com.redhat.ceylon.compiler.typechecker.tree.Tree;
import com.redhat.ceylon.model.typechecker.model.*;
import org.intellij.plugins.ceylon.ide.annotator.TypeCheckerProvider;
import org.intellij.plugins.ceylon.ide.ceylonCode.doc.IdeaDocGenerator;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonLanguage;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonFile;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonTokens;
import org.intellij.plugins.ceylon.ide.ceylonCode.resolve.CeylonReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class CeylonDocProvider extends AbstractDocumentationProvider {

    // A few common element types we know will never trigger a doc popup
    private static final List<IElementType> TYPES_TO_IGNORE = Arrays.asList(
            CeylonTokens.WS, CeylonTokens.LINE_COMMENT, CeylonTokens.COMMA, CeylonTokens.SEMICOLON,
            CeylonTokens.LBRACE, CeylonTokens.RBRACE,
            CeylonTokens.LBRACKET, CeylonTokens.RBRACKET,
            CeylonTokens.LPAREN, CeylonTokens.RPAREN
    );

    @Nullable
    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        try {
            TypeChecker tc = TypeCheckerProvider.getFor(element);
            IdeaDocGenerator generator = new IdeaDocGenerator(tc);
            if (element instanceof DummyPsiElement) {
                Referenceable referenceable = ((DummyPsiElement) element).referenceable;
                PhasedUnit pu = tc.getPhasedUnitFromRelativePath(referenceable.getUnit().getRelativePath());
                Tree.CompilationUnit cu = pu.getCompilationUnit();
                return generator.getDocumentationText(referenceable, null, cu, generator.DocParams$new$(pu, element.getProject())).value;
            }
            if (element.getContainingFile() != null) {
                Tree.CompilationUnit cu = ((CeylonFile) element.getContainingFile()).getCompilationUnit();
                PhasedUnit pu = tc.getPhasedUnitFromRelativePath(cu.getUnit().getRelativePath());
                return Objects.toString(generator.getDocumentation(cu, element.getTextOffset(), generator.DocParams$new$(pu, element.getProject())), null);
            }
        } catch (ceylon.language.AssertionError | Exception e) {
            e.printStackTrace();
            throw e;
        }
        return null;
    }

    @Nullable
    @Override
    public PsiElement getCustomDocumentationElement(@NotNull Editor editor, @NotNull PsiFile file, @Nullable PsiElement contextElement) {
        if (contextElement != null && !TYPES_TO_IGNORE.contains(contextElement.getNode().getElementType())) {
            return contextElement;
        }

        return null;
    }

    // FIXME refactor with com.redhat.ceylon.eclipse.code.hover.DocumentationHover.getLinkedModel(String, TypeChecker)
    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        String[] bits = link.split(":");
        String moduleNameAndVersion = bits[1];
        int loc = moduleNameAndVersion.indexOf('/');
        String moduleName = moduleNameAndVersion.substring(0, loc);
        String moduleVersion = moduleNameAndVersion.substring(loc + 1);

        TypeChecker tc = TypeCheckerProvider.getFor(context);

        Module theModule = null;

        for (Module module : tc.getContext().getModules().getListOfModules()) {
            if (module.getNameAsString().equals(moduleName) && module.getVersion().equals(moduleVersion)) {
                theModule = module;
            }
        }

        if (theModule == null || bits.length == 2) {
            return new DummyPsiElement(theModule, context.getContainingFile());
        }

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
            if (Objects.equals(bits[0], "doc")) {
                return new DummyPsiElement(target, context.getContainingFile());
            } else if (Objects.equals(bits[0], "dec")) {
                CeylonCompositeElement psiDecl = CeylonReference.resolveDeclaration(target, tc, context.getProject());

                if (psiDecl != null) {
                    psiDecl.navigate(true);
                }
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
        public ItemPresentation getPresentation() {
            return new ItemPresentation() {
                @Nullable
                @Override
                public String getPresentableText() {
                    return referenceable.getNameAsString();
                }

                @Nullable
                @Override
                public String getLocationString() {
                    return null;
                }

                @Nullable
                @Override
                public Icon getIcon(boolean unused) {
                    return null;
                }
            };
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

