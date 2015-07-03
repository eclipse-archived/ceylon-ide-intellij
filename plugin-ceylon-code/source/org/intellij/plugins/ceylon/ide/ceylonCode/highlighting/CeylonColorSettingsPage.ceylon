import ceylon.interop.java {
    createJavaObjectArray,
    javaString
}

import com.intellij.openapi.editor {
    DefaultLanguageHighlighterColors
}
import com.intellij.openapi.editor.colors {
    TextAttributesKey {
        createTextAttributesKey
    },
    EditorColors,
    CodeInsightColors
}
import com.intellij.openapi.options.colors {
    ColorSettingsPage,
    AttributesDescriptor,
    ColorDescriptor
}
import com.intellij.openapi.util {
    IconLoader
}

import java.lang {
    JString=String,
    ObjectArray
}
import java.util {
    JMap=Map,
    HashMap
}

import javax.swing {
    Icon
}


shared abstract class AbstractCeylonColorSettingsPage() satisfies ColorSettingsPage {
    
    AttributesDescriptor[] ourDescriptors = [
        AttributesDescriptor("Other unqualified identifiers", ceylonHighlightingColors.identifier),
        AttributesDescriptor("Type identifiers", ceylonHighlightingColors.type),
        AttributesDescriptor("String interpolation delimiters", ceylonHighlightingColors.typeLiteral),
        AttributesDescriptor("Keywords", ceylonHighlightingColors.keyword),
        AttributesDescriptor("Numeric literals", ceylonHighlightingColors.number),
        AttributesDescriptor("Comments", ceylonHighlightingColors.comment),
        AttributesDescriptor("String literals", ceylonHighlightingColors.strings),
        AttributesDescriptor("Character literals", ceylonHighlightingColors.char),
        AttributesDescriptor("Interpolated", ceylonHighlightingColors.interp),
        AttributesDescriptor("Strings in annotations", ceylonHighlightingColors.annotationString),
        AttributesDescriptor("Annotations", ceylonHighlightingColors.annotation),
        AttributesDescriptor("Todos", ceylonHighlightingColors.todo),
        AttributesDescriptor("Semicolons", ceylonHighlightingColors.semi),
        AttributesDescriptor("Braces", ceylonHighlightingColors.brace),
        AttributesDescriptor("Package identifiers", ceylonHighlightingColors.packages),
        AttributesDescriptor("Other qualified identifiers", ceylonHighlightingColors.member)
    ];

    JMap<JString,TextAttributesKey> ourTags = HashMap<JString,TextAttributesKey>();
    ourTags.put(javaString("anno"), ceylonHighlightingColors.annotation);
    ourTags.put(javaString("interp"), ceylonHighlightingColors.interp);
    ourTags.put(javaString("stringInAnno"), ceylonHighlightingColors.annotationString);
    ourTags.put(javaString("member"), ceylonHighlightingColors.member);
    ourTags.put(javaString("pkg"), ceylonHighlightingColors.packages);
    
    shared actual JMap<JString,TextAttributesKey> additionalHighlightingTagToDescriptorMap => ourTags;
    
    shared actual ObjectArray<AttributesDescriptor> attributeDescriptors => createJavaObjectArray(ourDescriptors);
    
    shared actual ObjectArray<ColorDescriptor> colorDescriptors => ColorDescriptor.\iEMPTY_ARRAY;
    
    shared actual String demoText => """import <pkg>ceylon</pkg>.<pkg>math</pkg>.<pkg>integer</pkg> { smallest }
                                        
                                        <anno>shared</anno> void run() {
                                            String myStr = "hello, world";
                                            print("myString=``<interp>myStr</interp>``");
                                            value number = 13.37;
                                            value char = 'a';
                                            Duck().<member>fly</member>();
                                        }
                                        
                                        // Cool class
                                        <anno>by</anno>(<stringInAnno>"Trompon"</stringInAnno>)
                                        class Duck() {
                                            <anno>shared</anno> void fly() {}
                                        }
                                      """;
    
    shared actual String displayName => "Ceylon";
    
    shared actual Icon icon => IconLoader.getIcon("/icons/ceylon.png");
    
}

shared object ceylonHighlightingColors {
    shared TextAttributesKey identifier = createTextAttributesKey("CEYLON_IDENTIFIER", DefaultLanguageHighlighterColors.\iIDENTIFIER);
    shared TextAttributesKey type = createTextAttributesKey("CEYLON_TYPE", DefaultLanguageHighlighterColors.\iCLASS_NAME);
    shared TextAttributesKey typeLiteral = createTextAttributesKey("CEYLON_TYPE_LITERAL", DefaultLanguageHighlighterColors.\iIDENTIFIER);
    shared TextAttributesKey keyword = createTextAttributesKey("CEYLON_KEYWORD", DefaultLanguageHighlighterColors.\iKEYWORD);
    shared TextAttributesKey number = createTextAttributesKey("CEYLON_NUMBER", DefaultLanguageHighlighterColors.\iNUMBER);
    shared TextAttributesKey comment = createTextAttributesKey("CEYLON_COMMENT", DefaultLanguageHighlighterColors.\iDOC_COMMENT);
    shared TextAttributesKey strings = createTextAttributesKey("CEYLON_STRING", DefaultLanguageHighlighterColors.\iSTRING);
    shared TextAttributesKey char = createTextAttributesKey("CEYLON_CHAR", DefaultLanguageHighlighterColors.\iSTRING);
    shared TextAttributesKey interp = createTextAttributesKey("CEYLON_INTERP", EditorColors.\iINJECTED_LANGUAGE_FRAGMENT);
    shared TextAttributesKey annotationString = createTextAttributesKey("CEYLON_ANNOTATION_STRING", DefaultLanguageHighlighterColors.\iSTRING);
    shared TextAttributesKey annotation = createTextAttributesKey("CEYLON_ANNOTATION", CodeInsightColors.\iANNOTATION_NAME_ATTRIBUTES);
    shared TextAttributesKey todo = createTextAttributesKey("CEYLON_TODO", CodeInsightColors.\iTODO_DEFAULT_ATTRIBUTES);
    shared TextAttributesKey semi = createTextAttributesKey("CEYLON_SEMI", DefaultLanguageHighlighterColors.\iSEMICOLON);
    shared TextAttributesKey brace = createTextAttributesKey("CEYLON_BRACE", DefaultLanguageHighlighterColors.\iBRACES);
    shared TextAttributesKey packages = createTextAttributesKey("CEYLON_PACKAGE", DefaultLanguageHighlighterColors.\iIDENTIFIER);
    shared TextAttributesKey member = createTextAttributesKey("CEYLON_MEMBER", CodeInsightColors.\iINSTANCE_FIELD_ATTRIBUTES);
}