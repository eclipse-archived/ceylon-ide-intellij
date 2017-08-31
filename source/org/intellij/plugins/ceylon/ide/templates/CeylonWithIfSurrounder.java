//package org.intellij.plugins.ceylon.ide.templates;
//
//import com.intellij.codeInsight.CodeInsightUtilCore;
//import com.intellij.lang.surroundWith.Surrounder;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.project.Project;
//import com.intellij.openapi.util.TextRange;
//import com.intellij.psi.PsiElement;
//import com.intellij.psi.PsiFile;
//import com.intellij.psi.PsiFileFactory;
//import com.intellij.psi.util.PsiTreeUtil;
//import com.intellij.psi.util.PsiUtilCore;
//import com.intellij.util.IncorrectOperationException;
//import org.intellij.plugins.ceylon.ide.lang.CeylonFileType;
//import org.intellij.plugins.ceylon.ide.psi.CeylonPsi;
//import org.jetbrains.annotations.NotNull;
//import org.jetbrains.annotations.Nullable;
//
//class CeylonWithIfSurrounder implements Surrounder {
//    @Override
//    public String getTemplateDescription() {
//        return "do you see me?";
//    }
//
//    @Override
//    public boolean isApplicable(@NotNull PsiElement[] elements) {
//        return true;
//    }
//
//    @Nullable
//    @Override
//    public TextRange surroundElements(@NotNull Project project, @NotNull Editor editor, @NotNull PsiElement[] elements) throws IncorrectOperationException {
//        String content = "void a(){if(true){\n}}";
//        PsiFile file = PsiFileFactory.getInstance(project).createFileFromText("dummy.ceylon", CeylonFileType.INSTANCE, content);
//
//        CeylonPsi.BooleanConditionPsi condition = PsiTreeUtil.getParentOfType(PsiUtilCore.getElementAtOffset(file, 12), CeylonPsi.BooleanConditionPsi.class);
//        CeylonPsi.IfStatementPsi ifStatement = PsiTreeUtil.getParentOfType(condition, CeylonPsi.IfStatementPsi.class);
//
//        assert condition != null;
//        assert ifStatement != null;
//
//        condition.replace(elements[0]);
//
//        ifStatement = (CeylonPsi.IfStatementPsi) elements[0].getParent().replace(ifStatement);
//
//        CeylonPsi.BlockPsi block = PsiTreeUtil.getChildOfType(ifStatement.getChildren()[0], CeylonPsi.BlockPsi.class);
//        if (block != null) {
//            block = CodeInsightUtilCore.forcePsiPostprocessAndRestoreElement(block);
//            return TextRange.from(block.getTextOffset() + 1, 0);
//        }
//
//        return null;
//    }
//}
