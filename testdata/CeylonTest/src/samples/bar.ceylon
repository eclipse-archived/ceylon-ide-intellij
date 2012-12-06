import samples.subpackage { SubPackageClass }

interface Plop {
    formal shared Void bar();
}

doc "My wonderful class" class Bar() {
    MySampleClass myClass;
    SubPackageClass myOtherClass;
    Integer foo;
    interface Plop {
        formal shared Void foo();
    }
    class PlopImpl() satisfies Plop {
        void poulpe() {
        }
        actual shared Integer bar() {
            return 0;
        }
    }
    doc "foo" throws (Integer) void hello() {
    }
}