import ceylon.collection {
    ArrayList
}
import ceylon.interop.java {
    javaClass
}

import com.intellij.codeInsight.completion {
    CompletionProgressIndicator,
    CompletionService
}
import com.intellij.codeInsight.lookup {
    LookupElement,
    LookupElementPresentation,
    LookupManager
}
import com.intellij.codeInsight.lookup.impl {
    EmptyLookupItem,
    LookupImpl,
    LookupCellRenderer
}
import com.intellij.ide.highlighter {
    HighlighterFactory
}
import com.intellij.ide.util.treeView {
    PresentableNodeDescriptor
}
import com.intellij.openapi.application {
    ApplicationManager
}
import com.intellij.openapi.fileTypes {
    FileTypeManager
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    TextRange
}
import com.intellij.ui {
    JBColor,
    SimpleColoredComponent,
    SimpleTextAttributes {
        merge
    }
}

import java.awt {
    Color,
    Component,
    Insets
}
import java.lang {
    ReflectiveOperationException,
    JString=String,
    Math,
    Iterable,
    Runnable
}

import javax.swing {
    JPanel,
    Icon,
    JList
}

shared void installCustomLookupCellRenderer(Project project) {
    if (is CompletionProgressIndicator currentCompletion
            = CompletionService.completionService.currentCompletion) {
        CustomLookupCellRenderer(currentCompletion.lookup, project).install();
    } else if (is LookupImpl activeLookup
            = LookupManager.getInstance(project).activeLookup) {
        CustomLookupCellRenderer(activeLookup, project).install();
    }
}

shared alias Fragment => PresentableNodeDescriptor<Anything>.ColoredFragment;
Fragment createFragment(String text, SimpleTextAttributes atts)
        => PresentableNodeDescriptor<Anything>.ColoredFragment(text, atts);

shared class CustomLookupCellRenderer(LookupImpl lookup, Project project)
        extends LookupCellRenderer(lookup) {

    value prefixForegroundColor = JBColor(Color(176, 0, 176), Color(209, 122, 214));
    value selectedPrefixForegroundColor = JBColor(Color(249, 209, 211), Color(209, 122, 214));

    shared void install()
            => ApplicationManager.application
                .invokeLater(object satisfies Runnable {
            shared actual void run() {
                try {
                    value field
                            = javaClass<LookupImpl>()
                            .getDeclaredField("myCellRenderer");
                    field.accessible = true;
                    field.set(lookup, outer);
                    field.accessible = false;
                    value method
                            = javaClass<JList<out Object>>()
                            .getDeclaredMethod("setCellRenderer");
                    method.accessible = true;
                    method.invoke(lookup.list, outer);
                    method.accessible = false;
                }
                catch (ReflectiveOperationException e) {
                    e.printStackTrace();
                }
            }
        });

    shared actual Component getListCellRendererComponent(JList<out Object>? list,
            Object? element, Integer index, Boolean isSelected, Boolean hasFocus) {
        Component component
                = super.getListCellRendererComponent(list, element, index, isSelected, hasFocus);
        assert (is LookupElement element);
        customize(component, element, isSelected);
        return component;
    }

    void customize(Component comp, LookupElement element, Boolean isSelected) {
        if (is JPanel comp,
            is SimpleColoredComponent firstChild = comp.getComponent(0)) {
            value pres = LookupElementPresentation();
            element.renderElement(pres);
            assert (exists text = pres.itemText);
            value coloredComponent = firstChild;
            try {
                resetColoredComponent(coloredComponent);
                renderItemName {
                    item = element;
                    selected = isSelected && lookup.focused;
                    name = text;
                    nameComponent = coloredComponent;
                };
            }
            catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    void renderItemName(LookupElement item, Boolean selected, String name,
            SimpleColoredComponent nameComponent) {

        value colors =
                if (selected)
                then Singleton(createFragment(name,
                        SimpleTextAttributes(SimpleTextAttributes.stylePlain, JBColor.white)))
                else highlight(name, project);

        String prefix
                = if (item is EmptyLookupItem) then ""
                else lookup.itemPattern(item);
        value highlightedPrefixColor
                = SimpleTextAttributes(SimpleTextAttributes.stylePlain,
                    selected
                    then selectedPrefixForegroundColor
                    else prefixForegroundColor);
        value colorsWithPrefix =
            if (prefix.size>0, exists ranges = getMatchingFragments(prefix, name))
            then mergeHighlightAndMatches(colors, ranges, highlightedPrefixColor)
            else colors;

        for (color in colorsWithPrefix) {
            nameComponent.append(color.text, color.attributes);
        }
    }

    void resetColoredComponent(SimpleColoredComponent coloredComponent) {
        Icon icon = coloredComponent.icon;
        Insets ipad = coloredComponent.ipad;
        coloredComponent.clear();
        coloredComponent.setIcon(icon);
        coloredComponent.ipad = ipad;
    }

    List<Fragment> highlight(String text, Project project) {
        value highlighter
                = HighlighterFactory.createHighlighter(project,
                    FileTypeManager.instance.getFileTypeByFileName("coin.ceylon"));
        highlighter.setText(JString(text));
        value iterator = highlighter.createIterator(0);
        value fragments = ArrayList<Fragment>();
        while (!iterator.atEnd()) {
            value attr = iterator.textAttributes;
            String subtext = text.substring(iterator.start, iterator.end);
            fragments.add(createFragment(subtext,
                SimpleTextAttributes.fromTextAttributes(attr)));
            iterator.advance();
        }
        return fragments;
    }

}

shared List<Fragment> mergeHighlightAndMatches(List<Fragment> highlight,
        Iterable<TextRange> matches, SimpleTextAttributes highlighted) {

    value merged = ArrayList<Fragment>();
    value iterator = matches.iterator();
    variable TextRange? currentRange = iterator.hasNext() then iterator.next();
    variable Integer currentIndex = 0;
    variable Integer consumedFromRange = 0;
    for (fragment in highlight) {
        value range = currentRange;
        if (!exists range) {
            merged.add(fragment);
        }
        else if (currentIndex + fragment.text.size<range.startOffset) {
            merged.add(fragment);
        } else {
            variable Integer substart = 0;
            variable Integer sublength = 0;
            variable String subtext;
            variable Boolean rangeIsApplicable = true;
            while (rangeIsApplicable) {
                if (currentIndex<range.startOffset) {
                    sublength = range.startOffset - currentIndex;
                    subtext = fragment.text.substring(substart, sublength);
                    merged.add(createFragment(subtext, fragment.attributes));
                }
                if (range.endOffset>currentIndex +fragment.text.size) {
                    subtext = fragment.text.substring(sublength);
                    merged.add(createFragment(subtext, merge(fragment.attributes, highlighted)));
                    consumedFromRange += fragment.text.size - sublength;
                    rangeIsApplicable = false;
                } else {
                    if (consumedFromRange>0) {
                        Integer toConsume = range.length - consumedFromRange;
                        subtext = fragment.text.substring(0, Math.min(toConsume, fragment.text.size));
                        sublength = toConsume;
                    } else {
                        subtext = fragment.text.substring(sublength, sublength + range.length);
                        sublength += range.length;
                    }
                    merged.add(createFragment(subtext, merge(fragment.attributes, highlighted)));
                    currentRange = iterator.hasNext() then iterator.next();
                    consumedFromRange = 0;
                    value bool
                            = if (exists r = currentRange)
                            then r.startOffset >= currentIndex + fragment.text.size
                            else true;
                    if (bool) {
                        rangeIsApplicable = false;
                        if (sublength<fragment.text.size) {
                            subtext = fragment.text.substring(sublength);
                            merged.add(createFragment(subtext, fragment.attributes));
                        }
                    }
                }
                substart = sublength;
            }
        }
        currentIndex += fragment.text.size;
    }
    return merged;
}
