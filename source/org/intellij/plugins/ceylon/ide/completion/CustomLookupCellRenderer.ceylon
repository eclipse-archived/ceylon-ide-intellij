import ceylon.collection {
    ArrayList
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
    LookupImpl,
    LookupCellRenderer,
    EmptyLookupItem
}
import com.intellij.ide.util.treeView {
    PresentableNodeDescriptor {
        ColoredFragment
    }
}
import com.intellij.openapi.application {
    ApplicationManager
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
    SimpleTextAttributes
}
import com.redhat.ceylon.ide.common.util {
    escaping
}

import java.awt {
    Color,
    Component,
    Insets
}
import java.lang {
    Types {
        classForType
    },
    ReflectiveOperationException
}

import javax.swing {
    JPanel,
    Icon,
    JList,
    ListCellRenderer
}

import org.intellij.plugins.ceylon.ide.highlighting {
    ceylonHighlightingColors,
    hForegroundColor=foregroundColor
}
import org.intellij.plugins.ceylon.ide.settings {
    ceylonSettings
}

shared void installCustomLookupCellRenderer(Project project) {
    if (ceylonSettings.highlightedLabels) {
        if (is CompletionProgressIndicator currentCompletion
                = CompletionService.completionService.currentCompletion) {
            CustomLookupCellRenderer(currentCompletion.lookup, project).install();
        } else if (is LookupImpl activeLookup
                = LookupManager.getInstance(project).activeLookup) {
            CustomLookupCellRenderer(activeLookup, project).install();
        }
    }
}

shared class CustomLookupCellRenderer(LookupImpl lookup, Project project)
        extends LookupCellRenderer(lookup) {

    function brightness(Color color) => color.red + color.green + color.blue;
    value midBrightness = #80 * 3;

    function brighter(SimpleTextAttributes textAttributes, Color background) {
        return let (fg = textAttributes.fgColor,
                    bg = textAttributes.bgColor else background)
                SimpleTextAttributes(bg,
                    switch (brightness(bg) <=> midBrightness)
                    //light background:
                    case (larger)
                        if (brightness(fg) >= midBrightness)
                        then fg.darker().darker()
                        else fg.brighter().brighter()
                    //dark background:
                    case (smaller)
                        fg.brighter()
                    else Color.magenta,
                    textAttributes.waveColor,
                    textAttributes.style);
    }

    value searchMatch
            = SimpleTextAttributes(SimpleTextAttributes.styleSearchMatch, Color.black);

    function highlighted(ColoredFragment fragment, Boolean selected, Color background)
            => selected then searchMatch else brighter(fragment.attributes, background);

    shared void install()
            => ApplicationManager.application
                .invokeLater(() {
                    try {
                        value field
                                = classForType<LookupImpl>()
                                .getDeclaredField("myCellRenderer");
                        field.accessible = true;
                        field.set(lookup, this);
                        field.accessible = false;
                        value method
                                = classForType<JList<out Object>>()
                                .getDeclaredMethod("setCellRenderer",
                                    classForType<ListCellRenderer<out Object>>());
                        method.invoke(lookup.list, this);
                    }
                    catch (ReflectiveOperationException e) {
                        e.printStackTrace();
                    }
                });

    shared actual Component getListCellRendererComponent(JList<out Object>? list,
            Object? element, Integer index, Boolean isSelected, Boolean hasFocus) {
        value component
                = super.getListCellRendererComponent(list,
                        element, index, isSelected, hasFocus);
        if (is LookupElement element, !is EmptyLookupItem element) {
            customize(component, element, isSelected);
        }
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

    Color color(String token/*, Boolean qualifiedNameIsPath*/) {
        if (token.whitespace
              || token.size==1 && token in "()[]{}<>,.+*&|?;="
              || token == "...") {
            return JBColor.foreground();
        }
        else if (token.startsWith("\"") && token.endsWith("\"")) {
            return hForegroundColor(ceylonHighlightingColors.strings);
        }
        else if (token in escaping.keywords) {
            return hForegroundColor(ceylonHighlightingColors.keyword);
        }
        else {
            assert (exists first = token[0]);
            value key
                = /*if (qualifiedNameIsPath) then ceylonHighlightingColors.packages
                else*/
                if (first.uppercase)
                then ceylonHighlightingColors.type
                else ceylonHighlightingColors.identifier;
            return hForegroundColor(key);
        }
    }

    ColoredFragment[] colorizeTokens(Boolean selected, String text, Integer style/*, Boolean qualifiedNameIsPath*/) {
        if (selected) {
            return Singleton(ColoredFragment(text, SimpleTextAttributes(style, JBColor.white)));
        }
        else if (text.startsWith("shared actual ")) {
            value color = hForegroundColor(ceylonHighlightingColors.annotation);
            return [
                ColoredFragment("shared actual ", SimpleTextAttributes(style, color)),
                *colorizeTokens(selected, text[14...], style/*, qualifiedNameIsPath*/)
            ];
        }
        else {
            /*value pattern = qualifiedNameIsPath then "()[]{}<>,+*&|?;:= " else "()[]{}<>,.+*&|?;:= ";*/
            variable value quoted = false;
            value tokens
                    = text.split {
                        discardSeparators = false;
                        groupSeparators = false;
                        function splitting(Character ch) {
                            if (ch=='"') {
                                quoted = !quoted;
                                return false;
                            }
                            else if (quoted) {
                                return false;
                            }
                            else {
                                return ch in "()[]{}<>,.+*&|?;:= ";
                            }
                        }
                    };
            return [
                for (token in tokens) if (!token.empty)
                ColoredFragment(token, SimpleTextAttributes(style, color(token/*, qualifiedNameIsPath*/)))
            ];

        }
    }

    void renderItemName(LookupElement item, Boolean selected, String name,
            SimpleColoredComponent nameComponent, Boolean strikeout) {

        value colors
                = colorizeTokens {
                    text = name;
                    selected = selected;
                    style = strikeout
                        then SimpleTextAttributes.styleStrikeout
                        else SimpleTextAttributes.stylePlain;
                    /*qualifiedNameIsPath
                        = item.\iobject
                        is ModuleVersionDetails|Package|Module;*/
                };

        String prefix = lookup.itemPattern(item);

        value lookupString = item.lookupString;
        value realLookupString
                = if (exists loc = lookupString.lastOccurrence(':'))
                then lookupString[0:loc] else lookupString;
        value colorsWithPrefix
                = if (/*selected &&*/ !prefix.empty,
                      exists ranges = getMatchingFragments(prefix, item.lookupString))
                then let (it = ranges.iterator())
                mergeHighlightAndMatches {
                    highlight = colors;
                    background = nameComponent.background;
                    selected = selected;
                    from = name.firstInclusion(realLookupString) else 0;
                    nextMatch() => it.hasNext() then it.next();
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

    shared List<ColoredFragment> mergeHighlightAndMatches(List<ColoredFragment> highlight,
            Integer from, TextRange? nextMatch(), Boolean selected, Color background) {

        value merged = ArrayList<ColoredFragment>();
        variable value currentRange = nextMatch();
        variable value currentIndex = 0;
        for (fragment in highlight) {
            value text = fragment.text;
            value size = text.size;
            value initialRange = currentRange;
            if (!exists initialRange) {
                merged.add(fragment);
            }
            else if (currentIndex + size <= initialRange.startOffset + from) {
                merged.add(fragment);
            }
            else {
                variable value substart = 0;
                variable value sublength = 0;
                variable value consumedFromRange = 0;
                while (exists range = currentRange) {

                    if (currentIndex < range.startOffset + from) {
                        sublength = range.startOffset + from - currentIndex;
                        String subtext = text.substring(substart, sublength);
                        merged.add(ColoredFragment(subtext, fragment.attributes));
                    }

                    if (range.endOffset + from > currentIndex + size) {
                        String subtext = text[sublength...];
                        merged.add(ColoredFragment(subtext, highlighted(fragment, selected, background)));
                        consumedFromRange += size - sublength;
                        currentRange = null;
                    }
                    else {

                        String subtext;
                        if (consumedFromRange > 0) {
                            value toConsume = range.length - consumedFromRange;
                            subtext = text[0:toConsume];
                            sublength = toConsume;
                        }
                        else {
                            subtext = text[sublength:range.length];
                            sublength += range.length;
                        }
                        merged.add(ColoredFragment(subtext, highlighted(fragment, selected, background)));

                        value nextRange = nextMatch();
                        consumedFromRange = 0;
                        if (exists nextRange,
                            nextRange.startOffset + from < currentIndex + size) {
                            currentRange = nextRange;
                        }
                        else {
                            if (sublength < size) {
                                String rest = text[sublength...];
                                merged.add(ColoredFragment(rest, fragment.attributes));
                            }
                            currentRange = null;
                        }

                    }
                    substart = sublength;

                }
            }
            currentIndex += size;

        }
        return merged;
    }

}
