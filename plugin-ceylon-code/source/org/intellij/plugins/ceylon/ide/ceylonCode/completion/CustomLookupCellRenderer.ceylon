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
import com.intellij.openapi.editor.markup {
    EffectType
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
    Runnable
}

import javax.swing {
    JPanel,
    Icon,
    JList,
    ListCellRenderer
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
                            .getDeclaredMethod("setCellRenderer",
                                javaClass<ListCellRenderer<out Object>>());
                    method.invoke(lookup.list, outer);
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
            is SimpleColoredComponent coloredComponent = comp.getComponent(0)) {
            value pres = LookupElementPresentation();
            element.renderElement(pres);
            assert (exists text = pres.itemText);
            try {
                resetColoredComponent(coloredComponent);
                renderItemName {
                    item = element;
                    selected = isSelected && lookup.focused;
                    name = text;
                    nameComponent = coloredComponent;
                    strikeout = pres.strikeout;
                };
            }
            catch (ReflectiveOperationException e) {
                e.printStackTrace();
            }
        }
    }

    void renderItemName(LookupElement item, Boolean selected, String name,
            SimpleColoredComponent nameComponent, Boolean strikeout) {

        value style
                = strikeout
                then SimpleTextAttributes.styleStrikeout
                else SimpleTextAttributes.stylePlain;
        value colors
                = selected
                then Singleton(createFragment(name,
                        SimpleTextAttributes(style, JBColor.white)))
                else highlight(name, project, strikeout);

        String prefix
                = item is EmptyLookupItem
                then ""
                else lookup.itemPattern(item);
        value highlightedPrefixColor
                = SimpleTextAttributes(style,
                    selected
                        then selectedPrefixForegroundColor
                        else prefixForegroundColor);
        value colorsWithPrefix
                = if (prefix.size>0,
                      exists ranges = getMatchingFragments(prefix, name))
                then let (it = ranges.iterator())
                mergeHighlightAndMatches {
                    highlight = colors;
                    nextMatch() => it.hasNext() then it.next();
                    highlighted = highlightedPrefixColor;
                }
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

    List<Fragment> highlight(String text, Project project, Boolean strikeout) {
        value highlighter
                = HighlighterFactory.createHighlighter(project,
                    FileTypeManager.instance.getFileTypeByFileName("coin.ceylon"));
        highlighter.setText(JString(text));
        value iterator = highlighter.createIterator(0);
        value fragments = ArrayList<Fragment>();
        while (!iterator.atEnd()) {
            String subtext = text.substring(iterator.start, iterator.end);
            value attr = iterator.textAttributes;
            if (strikeout) {
                attr.effectType = EffectType.strikeout;
                attr.effectColor = Color.black;
            }
            value attributes = SimpleTextAttributes.fromTextAttributes(attr);
            fragments.add(createFragment(subtext, attributes));
            iterator.advance();
        }
        return fragments;
    }

}

shared List<Fragment> mergeHighlightAndMatches(List<Fragment> highlight,
        TextRange? nextMatch(), SimpleTextAttributes highlighted) {

    value merged = ArrayList<Fragment>();
    variable Integer currentIndex = 0;
    for (fragment in highlight) {
        value initialRange = nextMatch();
        value text = fragment.text;
        value size = text.size;
        if (!exists initialRange) {
            merged.add(fragment);
        }
        else if (currentIndex + size < initialRange.startOffset) {
            merged.add(fragment);
        }
        else {
            variable Integer substart = 0;
            variable Integer sublength = 0;
            variable TextRange? currentRange = initialRange;
            variable Integer consumedFromRange = 0;
            while (exists range = currentRange) {

                if (currentIndex < range.startOffset) {
                    sublength = range.startOffset - currentIndex;
                    String subtext = text.substring(substart, sublength);
                    merged.add(createFragment(subtext, fragment.attributes));
                }

                if (range.endOffset > currentIndex + size) {
                    String subtext = text[sublength...];
                    merged.add(createFragment(subtext, merge(fragment.attributes, highlighted)));
                    consumedFromRange += size - sublength;
                    currentRange = null;
                }
                else {

                    String subtext;
                    if (consumedFromRange > 0) {
                        Integer toConsume = range.length - consumedFromRange;
                        subtext = text[0:toConsume];
                        sublength = toConsume;
                    }
                    else {
                        subtext = text[sublength:range.length];
                        sublength += range.length;
                    }
                    merged.add(createFragment(subtext, merge(fragment.attributes, highlighted)));

                    value nextRange = nextMatch();
                    consumedFromRange = 0;
                    if (exists nextRange,
                        nextRange.startOffset < currentIndex + size) {
                        currentRange = nextRange;
                    }
                    else {
                        if (sublength < size) {
                            String rest = text[sublength...];
                            merged.add(createFragment(rest, fragment.attributes));
                        }
                        currentRange = null;
                    }

                }
                substart = sublength;
            }
        }
        currentIndex += text.size;
    }
    return merged;
}
