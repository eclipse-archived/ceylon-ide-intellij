import com.intellij.navigation {
    ItemPresentationProvider,
    ItemPresentation
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Scope,
    Declaration
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
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
            => object satisfies ItemPresentation {
        getIcon(Boolean unused) => icons.forDeclaration(item.declaration);
        locationString => nestedLocation(item.declaration);
        presentableText => nestedName(item.declaration);
    };
}
