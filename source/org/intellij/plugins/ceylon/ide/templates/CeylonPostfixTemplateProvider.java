//package org.intellij.plugins.ceylon.ide.templates;
//
//import com.intellij.codeInsight.template.postfix.templates.PostfixTemplate;
//import com.intellij.codeInsight.template.postfix.templates.PostfixTemplateProvider;
//import com.intellij.openapi.editor.Editor;
//import com.intellij.openapi.editor.impl.EditorImpl;
//import com.intellij.openapi.util.Key;
//import com.intellij.openapi.vfs.VirtualFile;
//import com.intellij.psi.PsiFile;
//import org.jetbrains.annotations.NotNull;
//
//import java.util.HashSet;
//import java.util.Set;
//
//public class CeylonPostfixTemplateProvider implements PostfixTemplateProvider {
//
//    public static final Key<VirtualFile> ORIG_VFILE = Key.create("ORIG_VFILE");
//    private final Set<PostfixTemplate> templates = new HashSet<>();
//
//    public CeylonPostfixTemplateProvider() {
//        templates.add(new ExistsPostfixTemplate());
//    }
//
//    @NotNull
//    @Override
//    public Set<PostfixTemplate> getTemplates() {
//        return templates;
//    }
//
//    @Override
//    public boolean isTerminalSymbol(char currentChar) {
//        return currentChar == '.';
//    }
//
//    @Override
//    public void preExpand(@NotNull PsiFile file, @NotNull Editor editor) {
//    }
//
//    @Override
//    public void afterExpand(@NotNull PsiFile file, @NotNull Editor editor) {
//    }
//
//    @NotNull
//    @Override
//    public PsiFile preCheck(@NotNull PsiFile copyFile, @NotNull Editor realEditor, int currentOffset) {
//        // This is necessary to query the typechecker later, because we won't have access to the original PsiFile then
//        copyFile.putUserData(ORIG_VFILE, ((EditorImpl) realEditor).getVirtualFile());
//        return copyFile;
//    }
//}
