import com.intellij.navigation {
    ItemPresentationProvider,
    ColoredItemPresentation
}
import com.intellij.openapi.editor.colors {
    CodeInsightColors
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Scope,
    Declaration
}

import org.intellij.plugins.ceylon.ide.util {
    icons
}

shared class DeclarationPresentationProvider()
        satisfies ItemPresentationProvider<DeclarationNavigationItem> {

    String nestedName(Declaration dec)
            => if (is ClassOrInterface type = dec.container)
            then nestedName(type) + "." + dec.name
            else dec.name;

    function locationAsString(Scope container)
            => "(``container.qualifiedNameString else "default package"``)";

    String nestedLocation(Declaration dec)
            => if (is ClassOrInterface type = dec.container)
            then nestedLocation(type)
            else locationAsString(dec.container);

    getPresentation(DeclarationNavigationItem item)
            => object satisfies ColoredItemPresentation {
        getIcon(Boolean unused) => icons.forDeclaration(item.declaration);
        locationString => nestedLocation(item.declaration);
        presentableText => nestedName(item.declaration);
        textAttributesKey
                => item.declaration.deprecated
                then CodeInsightColors.deprecatedAttributes
                else null;
    };
}
