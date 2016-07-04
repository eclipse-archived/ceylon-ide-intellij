package org.intellij.plugins.ceylon.ide.templates;

import com.intellij.codeInsight.CodeInsightUtilCore;
import com.intellij.lang.surroundWith.SurroundDescriptor;
import com.intellij.lang.surroundWith.Surrounder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.PsiWhiteSpace;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.intellij.plugins.ceylon.ide.ceylonCode.lang.CeylonFileType;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonCompositeElement;
import org.intellij.plugins.ceylon.ide.ceylonCode.psi.CeylonPsi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class CeylonWithIfSurrounder implements Surrounder, SurroundDescriptor {
    @Override
    public String getTemplateDescription() {
        return "'if' statement";
    }

    @Override
    public boolean isApplicable(@NotNull PsiElement[] elements) {
        return true;
    }

    @Nullable
    @Override
    public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements)
            throws IncorrectOperationException {

        String content = "void a(){if(true){throw;}}";

        PsiFile file
                = PsiFileFactory.getInstance(project)
                    .createFileFromText("dummy.ceylon",
                            CeylonFileType.INSTANCE,
                            content);

        CeylonPsi.IfStatementPsi ifStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 9,
                    CeylonPsi.IfStatementPsi.class, true);


        CeylonPsi.BlockPsi block
                = PsiTreeUtil.findElementOfClassAtOffset(file, 17,
                    CeylonPsi.BlockPsi.class, true);

        CeylonPsi.ThrowPsi throwStatement
                = PsiTreeUtil.findElementOfClassAtOffset(file, 18,
                    CeylonPsi.ThrowPsi.class, true);

        PsiElement parent = elements[0].getParent();

        for (PsiElement element: elements) {
            block.addBefore(element, throwStatement);
        }
        throwStatement.delete();

        PsiElement result = parent.addBefore(ifStatement, elements[0]);

        for (PsiElement element: elements) {
            element.delete();
        }

        result = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(result);

        int loc = result.getTextOffset() + result.getText().indexOf("true");
        return new TextRange(loc, loc+4);
    }

    private Condition<PsiElement> condition = new Condition<PsiElement>() {
        @Override
        public boolean value(PsiElement element) {
            return element instanceof CeylonPsi.StatementOrArgumentPsi;
        }
    };

    @NotNull
    @Override
    public PsiElement[] getElementsToSurround(PsiFile file, int selectionStart, int selectionEnd) {
        PsiElement start = file.findElementAt(selectionStart);
        PsiElement end = file.findElementAt(selectionEnd);
        if (start instanceof PsiWhiteSpace) {
            start = PsiTreeUtil.getNextSiblingOfType(start,
                    CeylonCompositeElement.class);
        }
        if (end instanceof PsiWhiteSpace) {
            end = PsiTreeUtil.getPrevSiblingOfType(end,
                    CeylonCompositeElement.class);
        }

        CeylonPsi.StatementOrArgumentPsi first
                = (CeylonPsi.StatementOrArgumentPsi)
                PsiTreeUtil.findFirstParent(start, condition);
        CeylonPsi.StatementOrArgumentPsi last
                = (CeylonPsi.StatementOrArgumentPsi)
                PsiTreeUtil.findFirstParent(end, condition);
        if (first==null || last==null) {
            return PsiElement.EMPTY_ARRAY;
        }

        List<PsiElement> list = new ArrayList<>();
        list.add(first);
        CeylonPsi.StatementOrArgumentPsi current = first;
        while (current!=last && current!=null) {
            current = PsiTreeUtil.getNextSiblingOfType(current,
                    CeylonPsi.StatementOrArgumentPsi.class);
            list.add(current);
        }

        return list.toArray(PsiElement.EMPTY_ARRAY);
    }

    @NotNull
    @Override
    public Surrounder[] getSurrounders() {
        return new Surrounder[] {this};
    }

    @Override
    public boolean isExclusive() {
        return false;
    }
}
