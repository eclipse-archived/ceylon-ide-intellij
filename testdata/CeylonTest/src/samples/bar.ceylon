import samples.subpackage { SubPackageClass }

shared class Point {
    shared Float x;
    shared Float y;
    SubPackageClass myClass;

    class Plop extends SubPackageClass() {
    }
}
