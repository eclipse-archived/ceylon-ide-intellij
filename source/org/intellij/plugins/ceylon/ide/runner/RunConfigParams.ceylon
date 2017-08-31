import com.redhat.ceylon.common {
    Backend
}

class RunConfigParams(moduleName, packageName, topLevel, backend) {

    shared String moduleName;
    shared String packageName;
    shared String topLevel;
    shared Backend backend;

    shared actual Boolean equals(Object that) {
        if (is RunConfigParams that) {
            return this.moduleName == that.moduleName
                && this.packageName == that.packageName
                && this.topLevel == that.topLevel
                && this.backend == that.backend;
        }
        else {
            return false;
        }
    }

    hash => moduleName.hash + packageName.hash + topLevel.hash + backend.hash;

}