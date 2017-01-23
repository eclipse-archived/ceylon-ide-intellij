/*
package org.intellij.plugins.ceylon.ide.completion;

import ceylon.interop.java.CeylonList;
import ceylon.interop.java.JavaList;
import com.intellij.ide.util.treeView.PresentableNodeDescriptor;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.util.containers.EmptyIterable;
import com.redhat.ceylon.compiler.java.language.AbstractCallable;
import org.jetbrains.annotations.NotNull;
import org.junit.Test;

import java.util.*;

import static org.intellij.plugins.ceylon.ide.completion.mergeHighlightAndMatches_.mergeHighlightAndMatches;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class CustomLookupCellRendererTest {

    private static final SimpleTextAttributes MATCH = new SimpleTextAttributes(0, JBColor.PINK);
    private static final SimpleTextAttributes ANNOTATION = new SimpleTextAttributes(0, JBColor.CYAN);
    private static final SimpleTextAttributes TYPE = new SimpleTextAttributes(0, JBColor.MAGENTA);
    private static final SimpleTextAttributes IDENTIFIER = new SimpleTextAttributes(0, JBColor.GREEN);
    private static final SimpleTextAttributes NORMAL = new SimpleTextAttributes(0, JBColor.BLACK);

    @NotNull
    public AbstractCallable<TextRange> getNextMatch(final Iterable<TextRange> matches) {
        return new AbstractCallable<TextRange>(null, null, "", (short)-1) {
            Iterator<TextRange> it = matches.iterator();
            @Override
            public TextRange $call$() {
                return it.hasNext() ? it.next() : null;
            }
        };
    }

    @Test
    public void testMergeHighlightAndMatches_noMatches() {
        EmptyIterable<TextRange> matches = new EmptyIterable<>();

        List<PresentableNodeDescriptor.ColoredFragment> sample = highlight();
        List<PresentableNodeDescriptor.ColoredFragment> actual;
        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, sample),
                getNextMatch(matches), MATCH));

        assertHighlights(sample, actual);
    }

    @Test
    public void testMergeHighlightAndMatches_matchInsideFragment() {
        Iterable<TextRange> matches = Collections.singletonList(new TextRange(1, 3));

        List<PresentableNodeDescriptor.ColoredFragment> actual;
        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, highlight()),
                getNextMatch(matches), MATCH));

        List<PresentableNodeDescriptor.ColoredFragment> expected = Arrays.asList(
                fragment("s", ANNOTATION),
                fragment("ha", MATCH),
                fragment("red ", ANNOTATION),
                fragment("String ", TYPE),
                fragment("fun ", IDENTIFIER),
                fragment("() {}", NORMAL)
        );
        assertHighlights(expected, actual);
    }

    @Test
    public void testMergeHighlightAndMatches_matchStartsFragment() {
        Iterable<TextRange> matches = Collections.singletonList(new TextRange(0, 3));

        List<PresentableNodeDescriptor.ColoredFragment> actual;
        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, highlight()),
                getNextMatch(matches), MATCH));

        List<PresentableNodeDescriptor.ColoredFragment> expected = Arrays.asList(
                fragment("sha", MATCH),
                fragment("red ", ANNOTATION),
                fragment("String ", TYPE),
                fragment("fun ", IDENTIFIER),
                fragment("() {}", NORMAL)
        );
        assertHighlights(expected, actual);
    }

    @Test
    public void testMergeHighlightAndMatches_matchEndsFragment() {
        Iterable<TextRange> matches = Collections.singletonList(new TextRange(4, 7));

        List<PresentableNodeDescriptor.ColoredFragment> actual;
        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, highlight()),
                getNextMatch(matches), MATCH));

        List<PresentableNodeDescriptor.ColoredFragment> expected = Arrays.asList(
                fragment("shar", ANNOTATION),
                fragment("ed ", MATCH),
                fragment("String ", TYPE),
                fragment("fun ", IDENTIFIER),
                fragment("() {}", NORMAL)
        );
        assertHighlights(expected, actual);
    }

    @Test
    public void testMergeHighlightAndMatches_matchOverlapsFragments() {
        Iterable<TextRange> matches = Collections.singletonList(new TextRange(3, 9));

        List<PresentableNodeDescriptor.ColoredFragment> actual;
        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, highlight()),
                getNextMatch(matches), MATCH));

        List<PresentableNodeDescriptor.ColoredFragment> expected = Arrays.asList(
                fragment("sha", ANNOTATION),
                fragment("red ", MATCH),
                fragment("St", MATCH),
                fragment("ring ", TYPE),
                fragment("fun ", IDENTIFIER),
                fragment("() {}", NORMAL)
        );
        assertHighlights(expected, actual);
    }

    @Test
    public void testMergeHighlightAndMatches_matchOverlapsLotsOfFragments() {
        Iterable<TextRange> matches = Collections.singletonList(new TextRange(3, 15));

        List<PresentableNodeDescriptor.ColoredFragment> actual;
        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, highlight()),
                getNextMatch(matches), MATCH));

        List<PresentableNodeDescriptor.ColoredFragment> expected = Arrays.asList(
                fragment("sha", ANNOTATION),
                fragment("red ", MATCH),
                fragment("String ", MATCH),
                fragment("f", MATCH),
                fragment("un ", IDENTIFIER),
                fragment("() {}", NORMAL)
        );
        assertHighlights(expected, actual);
    }

    @Test
    public void testMergeHighlightAndMatches_multipleMatchesInFragment() {
        Iterable<TextRange> matches = Arrays.asList(
                new TextRange(5, 6),
                new TextRange(10, 12)
        );

        List<PresentableNodeDescriptor.ColoredFragment> actual;
        List<PresentableNodeDescriptor.ColoredFragment> sample = Collections.singletonList(fragment("printStackTrace", NORMAL));

        actual = new JavaList(null, mergeHighlightAndMatches(new CeylonList(null, sample),
                getNextMatch(matches), MATCH));

        List<PresentableNodeDescriptor.ColoredFragment> expected = Arrays.asList(
                fragment("print", NORMAL),
                fragment("S", MATCH),
                fragment("tack", NORMAL),
                fragment("Tr", MATCH),
                fragment("ace", NORMAL)
        );
        assertHighlights(expected, actual);
    }

    private List<PresentableNodeDescriptor.ColoredFragment> highlight() {
        List<PresentableNodeDescriptor.ColoredFragment> fragments = new ArrayList<>();

        fragments.add(fragment("shared ", ANNOTATION));
        fragments.add(fragment("String ", TYPE));
        fragments.add(fragment("fun ", IDENTIFIER));
        fragments.add(fragment("() {}", NORMAL));

        return fragments;
    }

    private PresentableNodeDescriptor.ColoredFragment fragment(String text, SimpleTextAttributes attributes) {
        return new PresentableNodeDescriptor.ColoredFragment(text, attributes);
    }

    private void assertHighlights(List<PresentableNodeDescriptor.ColoredFragment> expected,
                                  List<PresentableNodeDescriptor.ColoredFragment> actual) {

        assertEquals(expected.size(), actual.size());

        Iterator<PresentableNodeDescriptor.ColoredFragment> iterator = actual.iterator();

        for (PresentableNodeDescriptor.ColoredFragment c1 : expected) {
            PresentableNodeDescriptor.ColoredFragment fragment = iterator.next();

            if (!StringUtil.equals(c1.getText(), fragment.getText())
                    || c1.getAttributes().getFgColor() != fragment.getAttributes().getFgColor()) {

                fail("Expected fragment \"" + c1.getText() + "\" with color " + c1.getAttributes().getFgColor().toString()
                    + " but got fragment \"" + fragment.getText() + "\" with color " + fragment.getAttributes().getFgColor().toString());
            }
        }
    }
}
*/
