import com.intellij.navigation {
    ItemPresentationProvider,
    ItemPresentation
}
import com.redhat.ceylon.model.typechecker.model {
    ClassOrInterface,
    Scope
}

import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}

shared class DeclarationPresentationProvider()
        satisfies ItemPresentationProvider<DeclarationNavigationItem> {

    getPresentation(DeclarationNavigationItem item) 
            => object satisfies ItemPresentation {

        getIcon(Boolean unused)
                => icons.forDeclaration(item.decl);

        function locationAsString(Scope container)
                => "(``container.qualifiedNameString else "default package"``)";

        locationString
                => let (dec = item.decl)
                if (is ClassOrInterface type = dec.container)
                then locationAsString(type.container)
                else locationAsString(dec.container);

        presentableText 
                => let (dec = item.decl)
                if (is ClassOrInterface type = dec.container)
                then type.name + "." + dec.name
                else dec.name;
    };
}
