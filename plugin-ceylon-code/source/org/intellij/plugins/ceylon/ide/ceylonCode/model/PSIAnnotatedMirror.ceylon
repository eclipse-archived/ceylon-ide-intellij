import ceylon.interop.java {
    JavaIterator,
    javaString,
    JavaCollection
}

import com.intellij.psi {
    PsiNamedElement,
    PsiModifierListOwner
}
import com.redhat.ceylon.model.loader.mirror {
    AnnotatedMirror,
    AnnotationMirror
}

import java.lang {
    JString=String
}
import java.util {
    AbstractMap,
    AbstractSet
}

shared class PSIAnnotatedMirror(PsiModifierListOwner&PsiNamedElement psi)
        satisfies AnnotatedMirror {

    variable Map<String,AnnotationMirror>? _annotations = null;
    value annotations
            => _annotations
            else (_annotations = doWithLock(()
                    => map {
                        if (exists ml = psi.modifierList)
                        for (a in ml.annotations)
                        if (exists name = a.qualifiedName)
                            name -> PSIAnnotation(a)
                    }));

    getAnnotation(String name)
            => annotations[name]
                //TODO make this nicer:
                else annotations.filterKeys((key) => key.replaceLast(".", "$") == name).first?.item;

    annotationNames => JavaStringMap(annotations).keySet();

    name = doWithLock(() => psi.name else "unknown");
}


class JavaStringMap<Item>(Map<String,Item> map)
        extends AbstractMap<JString,Item>() {

    containsKey(Object? key) => if (is JString key) then key.string in map.keys else false;

    containsValue(Object? item) => if (exists item) then item in map.items else false;

    get(Object? key) => if (is JString key) then map[key.string] else null;

    size() => map.size;

    empty => map.empty;

    values() => JavaCollection(map.items);

    keySet() => object extends AbstractSet<JString>() {

        iterator() => JavaIterator<JString>(
            map.map((key->item) => javaString(key))
                .iterator());

        size() => map.size;

        empty => map.empty;
    };

    entrySet() => object extends AbstractSet<Entry<JString,Item>>() {

        iterator() => JavaIterator<Entry<JString,Item>>(
            map.map((key->item)
                => SimpleEntry(javaString(key), item))
            .iterator());

        contains(Object? entry)
            => if (is Entry<out Anything,out Anything> entry,
                   is JString s = entry.key,
                   exists val = entry.\ivalue,
                   exists it = map[s])
            then val == it
            else false;

        size() => map.size;

        empty => map.empty;

    };

}