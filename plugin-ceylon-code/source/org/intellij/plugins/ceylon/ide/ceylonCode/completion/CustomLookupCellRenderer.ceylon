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
    LookupImpl,
    LookupCellRenderer
}
import com.intellij.ide.util.treeView {
    PresentableNodeDescriptor
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
import com.redhat.ceylon.cmr.api {
    ModuleVersionDetails
}
import com.redhat.ceylon.ide.common.util {
    escaping
}
import com.redhat.ceylon.model.typechecker.model {
    Package,
    Module
}

import java.awt {
    Color,
    Component,
    Insets
}
import java.lang {
    ReflectiveOperationException,
    Runnable
}

import javax.swing {
    JPanel,
    Icon,
    JList,
    ListCellRenderer
}

import org.intellij.plugins.ceylon.ide.ceylonCode.highlighting {
    ceylonHighlightingColors,
    foregroundColor
}
import org.intellij.plugins.ceylon.ide.ceylonCode.settings {
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

shared alias Fragment => PresentableNodeDescriptor<Anything>.ColoredFragment;
Fragment createFragment(String text, SimpleTextAttributes atts)
        => PresentableNodeDescriptor<Anything>.ColoredFragment(text, atts);

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

    function highlighted(Fragment fragment, Boolean selected, Color background)
            => selected then searchMatch else brighter(fragment.attributes, background);

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

    Color color(String token/*, Boolean qualifiedNameIsPath*/) {
        if (token.whitespace
              || token.size==1 && token in "()[]{}<>,.+*&|?;="
              || token == "...") {
            return JBColor.foreground();
        }
        else if (token.startsWith("\"") && token.endsWith("\"")) {
            return foregroundColor(ceylonHighlightingColors.strings);
        }
        else if (token in escaping.keywords) {
            return foregroundColor(ceylonHighlightingColors.keyword);
        }
        else {
            assert (exists first = token[0]);
            value key
                = /*if (qualifiedNameIsPath) then ceylonHighlightingColors.packages
                else*/ if (first.uppercase) then ceylonHighlightingColors.type
                else ceylonHighlightingColors.identifier;
            return foregroundColor(key);
        }
    }

    Fragment[] colorizeTokens(Boolean selected, String text, Integer style/*, Boolean qualifiedNameIsPath*/) {
        if (selected) {
            return Singleton(createFragment(text, SimpleTextAttributes(style, JBColor.white)));
        }
        else if (text.startsWith("shared actual ")) {
            value color = foregroundColor(ceylonHighlightingColors.annotation);
            return [
                createFragment("shared actual ", SimpleTextAttributes(style, color)),
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
                createFragment(token, SimpleTextAttributes(style, color(token/*, qualifiedNameIsPath*/)))
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

    shared List<Fragment> mergeHighlightAndMatches(List<Fragment> highlight,
            Integer from, TextRange? nextMatch(), Boolean selected, Color background) {

        value merged = ArrayList<Fragment>();
        variable value currentRange = nextMatch();
        variable Integer currentIndex = 0;
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
                variable Integer substart = 0;
                variable Integer sublength = 0;
                variable Integer consumedFromRange = 0;
                while (exists range = currentRange) {

                    if (currentIndex < range.startOffset + from) {
                        sublength = range.startOffset + from - currentIndex;
                        String subtext = text.substring(substart, sublength);
                        merged.add(createFragment(subtext, fragment.attributes));
                    }

                    if (range.endOffset + from > currentIndex + size) {
                        String subtext = text[sublength...];
                        merged.add(createFragment(subtext, highlighted(fragment, selected, background)));
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
                        merged.add(createFragment(subtext, highlighted(fragment, selected, background)));

                        value nextRange = nextMatch();
                        consumedFromRange = 0;
                        if (exists nextRange,
                            nextRange.startOffset + from < currentIndex + size) {
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
            currentIndex += size;

        }
        return merged;
    }

}
