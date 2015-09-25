package org.intellij.plugins.ceylon.ide.ceylonCode.completion;

import ceylon.interop.java.javaString_;
import com.intellij.codeInsight.completion.CompletionProcess;
import com.intellij.codeInsight.completion.CompletionProgressIndicator;
import com.intellij.codeInsight.completion.CompletionService;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementPresentation;
import com.intellij.codeInsight.lookup.impl.EmptyLookupItem;
import com.intellij.codeInsight.lookup.impl.LookupCellRenderer;
import com.intellij.codeInsight.lookup.impl.LookupImpl;
import com.intellij.ide.highlighter.HighlighterFactory;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.highlighter.EditorHighlighter;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.FileTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.intellij.ui.SimpleTextAttributes.merge;

public class CustomLookupCellRenderer extends LookupCellRenderer {

    private static final Color PREFIX_FOREGROUND_COLOR = new JBColor(new Color(176, 0, 176), new Color(209, 122, 214));
    private static final Color SELECTED_PREFIX_FOREGROUND_COLOR = new JBColor(new Color(249, 236, 204), new Color(209, 122, 214));
    private static final Color SELECTED_BACKGROUND_COLOR = new Color(122, 186, 232);

    private LookupImpl myLookup;
    private Project project;

    @SuppressWarnings("unused")
    public static void install(final Project project) {
        CompletionProcess currentCompletion = CompletionService.getCompletionService().getCurrentCompletion();
        if (currentCompletion instanceof CompletionProgressIndicator) {
            final LookupImpl lookup = ((CompletionProgressIndicator) currentCompletion).getLookup();

            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    CustomLookupCellRenderer inst = new CustomLookupCellRenderer(lookup, project);
                    try {
                        Field f = LookupImpl.class.getDeclaredField("myCellRenderer");
                        f.setAccessible(true);
                        f.set(lookup, inst);
                        f.setAccessible(false);
                    } catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                    lookup.getList().setCellRenderer(inst);
                }
            });
        }
    }

    public CustomLookupCellRenderer(LookupImpl lookup, Project project) {
        super(lookup);
        myLookup = lookup;
        this.project = project;
    }

    @Override
    public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean hasFocus) {
        Component comp = super.getListCellRendererComponent(list, value, index, isSelected, hasFocus);

        if (!myLookup.isFocused()) {
            isSelected = false;
        }

        if (comp instanceof JPanel) {
            JPanel panel = (JPanel) comp;
            Component firstChild = panel.getComponent(0);

            if (isSelected) {
                panel.getComponent(0).setBackground(SELECTED_BACKGROUND_COLOR);
                panel.getComponent(1).setBackground(SELECTED_BACKGROUND_COLOR);
                panel.getComponent(2).setBackground(SELECTED_BACKGROUND_COLOR);
            }

            if (firstChild instanceof SimpleColoredComponent) {
                LookupElementPresentation pres = new LookupElementPresentation();

                ((LookupElement) value).renderElement(pres);

                String text = pres.getItemText();

                SimpleColoredComponent coloredComponent = (SimpleColoredComponent) firstChild;
                try {
                    resetColoredComponent(coloredComponent);
                    renderItemName((LookupElement) value, isSelected, text, coloredComponent);
                } catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        }
        return comp;
    }

    private void renderItemName(LookupElement item, boolean selected, String name,
                                SimpleColoredComponent nameComponent) throws ReflectiveOperationException {

        int style = SimpleTextAttributes.STYLE_PLAIN;
        List<PresentableNodeDescriptor.ColoredFragment> colors = highlight(name, project);

        final String prefix = item instanceof EmptyLookupItem ? "" : myLookup.itemPattern(item);
        if (prefix.length() > 0) {
            Iterable<TextRange> ranges = getMatchingFragments(prefix, name);
            if (ranges != null) {
                SimpleTextAttributes highlighted =
                        new SimpleTextAttributes(style, selected ? SELECTED_PREFIX_FOREGROUND_COLOR : PREFIX_FOREGROUND_COLOR);
                colors = mergeHighlightAndMatches(colors, ranges, highlighted);
            }
        }

        applyColors(nameComponent, colors);
    }

    private void applyColors(SimpleColoredComponent component, List<PresentableNodeDescriptor.ColoredFragment> colors) {
        for (PresentableNodeDescriptor.ColoredFragment color : colors) {
            component.append(color.getText(), color.getAttributes());
        }
    }

    private void resetColoredComponent(SimpleColoredComponent coloredComponent) {
        Icon icon = coloredComponent.getIcon();
        Insets iPad = coloredComponent.getIpad();
        coloredComponent.clear();
        coloredComponent.setIcon(icon);
        coloredComponent.setIpad(iPad);
    }

    private List<PresentableNodeDescriptor.ColoredFragment> highlight(String text, Project project) throws ReflectiveOperationException {
        EditorHighlighter highlighter = HighlighterFactory.createHighlighter(project, FileTypeManager.getInstance().getFileTypeByFileName("coin.ceylon"));
        highlighter.setText(javaString_.javaString(text));

        HighlighterIterator iterator = highlighter.createIterator(0);

        List<PresentableNodeDescriptor.ColoredFragment> fragments = new ArrayList<>();

        while (!iterator.atEnd()) {
            TextAttributes attr = iterator.getTextAttributes();
            String subtext = text.substring(iterator.getStart(), iterator.getEnd());

            fragments.add(new PresentableNodeDescriptor.ColoredFragment(subtext, SimpleTextAttributes.fromTextAttributes(attr)));

            iterator.advance();
        }

        return fragments;
    }

    public static List<PresentableNodeDescriptor.ColoredFragment> mergeHighlightAndMatches(List<PresentableNodeDescriptor.ColoredFragment> highlight,
                                                                                           Iterable<TextRange> matches, SimpleTextAttributes highlighted) {

        List<PresentableNodeDescriptor.ColoredFragment> merged = new ArrayList<>();
        Iterator<TextRange> iterator = matches.iterator();
        TextRange range = iterator.hasNext() ? iterator.next() : null;
        int currentIndex = 0;
        int consumedFromRange = 0;

        for (PresentableNodeDescriptor.ColoredFragment fragment : highlight) {
            if (range == null || currentIndex + fragment.getText().length() < range.getStartOffset()) {
                // next match is after this fragment
                merged.add(fragment);
            } else {
                int substart = 0;
                int sublength = 0;
                String subtext;
                boolean rangeIsApplicable = true;

                while (rangeIsApplicable) {
                    if (currentIndex < range.getStartOffset()) {
                        sublength = range.getStartOffset() - currentIndex;
                        subtext = fragment.getText().substring(substart, sublength);
                        merged.add(fragment(subtext, fragment.getAttributes()));
                    }

                    if (range.getEndOffset() > currentIndex + fragment.getText().length()) {
                        // current match continues after this fragment
                        subtext = fragment.getText().substring(sublength);
                        merged.add(fragment(subtext, merge(fragment.getAttributes(), highlighted)));
                        consumedFromRange += fragment.getText().length() - sublength;
                        rangeIsApplicable = false;
                    } else {
                        if (consumedFromRange > 0) {
                            int toConsume = range.getLength() - consumedFromRange;
                            subtext = fragment.getText().substring(0, Math.min(toConsume, fragment.getText().length()));
                            sublength = toConsume;
                        } else {
                            subtext = fragment.getText().substring(sublength, sublength + range.getLength());
                            sublength += range.getLength();
                        }
                        merged.add(fragment(subtext, merge(fragment.getAttributes(), highlighted)));
                        range = iterator.hasNext() ? iterator.next() : null;
                        consumedFromRange = 0;

                        if (range == null || range.getStartOffset() >= currentIndex + fragment.getText().length()) {
                            rangeIsApplicable = false;
                            if (sublength < fragment.getText().length()) {
                                subtext = fragment.getText().substring(sublength);
                                merged.add(fragment(subtext, fragment.getAttributes()));
                            }
                        }
                    }
                    substart = sublength;
                }
            }

            currentIndex += fragment.getText().length();
        }

        return merged;
    }

    private static PresentableNodeDescriptor.ColoredFragment fragment(String text, SimpleTextAttributes attributes) {
        return new PresentableNodeDescriptor.ColoredFragment(text, attributes);
    }
}
