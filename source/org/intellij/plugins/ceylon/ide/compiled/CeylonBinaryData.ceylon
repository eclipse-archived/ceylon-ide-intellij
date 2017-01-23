"Contains information that helps determining if a .class was compiled by the Ceylon compiler,
 and if it should be ignored."
shared class CeylonBinaryData(timestamp, ceylonBinary, ceylonIgnore, inner) {
    "To be compared to a VirtualFile's timestamp to check if the current data is outdated."
    shared Integer timestamp;
    "Does this class have a `@Ceylon` annotation?"
    shared Boolean ceylonBinary;
    "Does this class have a `@Ignore` annotation?"
    shared Boolean ceylonIgnore;
    "Is this an inner/anonymous class?"
    shared Boolean inner;

    "Was this class compiled by the Ceylon compiler?"
    shared Boolean ceylonCompiledFile => ceylonBinary || ceylonIgnore;
}