package org.intellij.plugins.ceylon.ide.ceylonCode.completion;

import ceylon.interop.java.javaString_;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.LookupElementRenderer;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

class ColoredTailElementRenderer extends LookupElementRenderer<LookupElement> {

    static ColoredTailElementRenderer INSTANCE = new ColoredTailElementRenderer();

    @Override
    public void renderElement(LookupElement element, LookupElementPresentation presentation) {
        if (element instanceof LookupElementBuilder) {
            LookupElementBuilder builder = (LookupElementBuilder) element;

            if (builder.getObject() instanceof ceylon.language.String) {
                String text = element.getLookupString();

                try {
                    Field myHardcodedPresentation = LookupElementBuilder.class.getDeclaredField("myHardcodedPresentation");
                    myHardcodedPresentation.setAccessible(true);
                    presentation.copyFrom((LookupElementPresentation) myHardcodedPresentation.get(builder));
                    myHardcodedPresentation.setAccessible(false);

                    presentation.clearTail();
                    Method appendTailText = LookupElementPresentation.class.getDeclaredMethod("appendTailText", LookupElementPresentation.TextFragment.class);

                    appendTailText.setAccessible(true);
                    color(text, null, appendTailText, presentation);
                    appendTailText.setAccessible(false);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        } else {
            throw new UnsupportedOperationException();
        }
    }

    private void color(String text, Project project, Method appendTailText, LookupElementPresentation presentation) throws ReflectiveOperationException {
        EditorHighlighter highlighter = HighlighterFactory.createHighlighter(project, FileTypeManager.getInstance().getFileTypeByFileName("coin.ceylon"));
        highlighter.setText(javaString_.javaString(text));

        HighlighterIterator iterator = highlighter.createIterator(0);

        while (!iterator.atEnd()) {
            TextAttributes attr = iterator.getTextAttributes();
            String subtext = text.substring(iterator.getStart(), iterator.getEnd());
            appendTailText.invoke(presentation, new LookupElementPresentation.TextFragment(subtext, false, attr.getForegroundColor()));

            iterator.advance();
        }

    }
}
