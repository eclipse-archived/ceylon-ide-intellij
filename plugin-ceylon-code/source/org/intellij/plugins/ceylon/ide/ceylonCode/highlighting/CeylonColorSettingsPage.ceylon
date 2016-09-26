import ceylon.interop.java {
    createJavaObjectArray,
    javaString
}

import com.intellij.openapi.editor {
    DefaultColors=DefaultLanguageHighlighterColors {
        strings=\iSTRING,
        ...
    }
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey {
        createKey=createTextAttributesKey
    },
    EditorColors,
    CodeInsightColors,
    EditorColorsManager
}
import com.intellij.openapi.editor.markup {
    TextAttributes
}
import com.intellij.openapi.options.colors {
    ColorSettingsPage,
    AttributesDescriptor,
    ColorDescriptor
}

import java.lang {
    JString=String
}
import java.util {
    HashMap
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}


shared abstract class AbstractCeylonColorSettingsPage() satisfies ColorSettingsPage {

    value ourDescriptors = [
        ["Other unqualified identifiers", ceylonHighlightingColors.identifier],
        ["Type identifiers", ceylonHighlightingColors.type],
        ["String interpolation delimiters", ceylonHighlightingColors.typeLiteral],
        ["Keywords", ceylonHighlightingColors.keyword],
        ["Numeric literals", ceylonHighlightingColors.number],
        ["Comments", ceylonHighlightingColors.comment],
        ["String literals", ceylonHighlightingColors.strings],
        ["Character literals", ceylonHighlightingColors.char],
        ["Interpolated", ceylonHighlightingColors.interp],
        ["Strings in annotations", ceylonHighlightingColors.annotationString],
        ["Annotations", ceylonHighlightingColors.annotation],
        ["Todos", ceylonHighlightingColors.todo],
        ["Semicolons", ceylonHighlightingColors.semi],
        ["Braces", ceylonHighlightingColors.brace],
        ["Package identifiers", ceylonHighlightingColors.packages],
        ["Other qualified identifiers", ceylonHighlightingColors.member]
    ]
    .collect(([name, key])=>AttributesDescriptor(name, key));

    value ourTags = HashMap<JString,TextAttributesKey>();
    ourTags[javaString("anno")] = ceylonHighlightingColors.annotation;
    ourTags[javaString("interp")] = ceylonHighlightingColors.interp;
    ourTags[javaString("stringInAnno")] = ceylonHighlightingColors.annotationString;
    ourTags[javaString("member")] = ceylonHighlightingColors.member;
    ourTags[javaString("pkg")] = ceylonHighlightingColors.packages;

    additionalHighlightingTagToDescriptorMap => ourTags;

    attributeDescriptors => createJavaObjectArray(ourDescriptors);

    colorDescriptors => ColorDescriptor.emptyArray;
    
    demoText => """import <pkg>ceylon</pkg>.<pkg>math</pkg>.<pkg>integer</pkg> { smallest }

                   <anno>shared</anno> void run() {
                       String myStr = "hello, world";
                       print("myString=<interp>``myStr``</interp>");
                       value number = 13.37;
                       value char = 'a';
                       Duck().<member>fly</member>();
                    }

                    // Cool class
                    <anno>by</anno>(<stringInAnno>"Trompon"</stringInAnno>)
                    class Duck() {
                        <anno>shared</anno> void fly() {}
                    }""";
    
    displayName => "Ceylon";

    icon => icons.ceylon;

}

shared object ceylonHighlightingColors {
    shared TextAttributesKey identifier = createKey("CEYLON_IDENTIFIER", DefaultColors.identifier);
    shared TextAttributesKey type = createKey("CEYLON_TYPE", DefaultColors.className);
    shared TextAttributesKey typeLiteral = createKey("CEYLON_TYPE_LITERAL", DefaultColors.identifier);
    shared TextAttributesKey keyword = createKey("CEYLON_KEYWORD", DefaultColors.keyword);
    shared TextAttributesKey number = createKey("CEYLON_NUMBER", DefaultColors.number);
    shared TextAttributesKey comment = createKey("CEYLON_COMMENT", DefaultColors.docComment);
    shared TextAttributesKey strings = createKey("CEYLON_STRING", DefaultColors.strings);
    shared TextAttributesKey char = createKey("CEYLON_CHARACTER", DefaultColors.strings);
    shared TextAttributesKey annotationString = createKey("CEYLON_ANNOTATION_STRING", DefaultColors.docComment);

    shared TextAttributesKey interp = createKey("CEYLON_INTERP", EditorColors.injectedLanguageFragment);
    shared TextAttributesKey annotationString = createKey("CEYLON_ANNOTATION_STRING", DefaultColors.strings);
    shared TextAttributesKey annotation = createKey("CEYLON_ANNOTATION", DefaultColors.metadata);
    shared TextAttributesKey todo = createKey("CEYLON_TODO", CodeInsightColors.todoDefaultAttributes);
    shared TextAttributesKey semi = createKey("CEYLON_SEMI", DefaultColors.semicolon);
    shared TextAttributesKey brace = createKey("CEYLON_BRACE", DefaultColors.braces);
    shared TextAttributesKey packages = createKey("CEYLON_PACKAGE", DefaultColors.identifier);
    shared TextAttributesKey member = createKey("CEYLON_MEMBER", DefaultColors.instanceField);
}

shared TextAttributes textAttributes(TextAttributesKey key)
        => EditorColorsManager.instance.globalScheme.getAttributes(key);