import com.redhat.ceylon.common {
    Backend
}

class RunConfigParams(mod, pkg, topLevel, backend) {

    shared String mod;
    shared String pkg;
    shared String topLevel;
    shared Backend backend;

    shared actual Boolean equals(Object that) {
        if (is RunConfigParams that) {
            return this.mod == that.mod
                && this.pkg == that.pkg
                && this.topLevel == that.topLevel
                && this.backend == that.backend;
        }
        else {
            return false;
        }
    }

    hash => mod.hash + pkg.hash + topLevel.hash + backend.hash;

}