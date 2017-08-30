import com.intellij.compiler.server {
    BuildProcessParametersProvider
}
import com.intellij.openapi.util.io {
    FileUtil
}

import java.lang {
    Str=String,
    Types {
        str=nativeString
    }
}
import java.util {
    ArrayList,
    List
}

import org.intellij.plugins.ceylon.ide.startup {
    CeylonIdePlugin
}

shared class BuildClasspathProvider() extends BuildProcessParametersProvider() {

    function compute() {
        value repo = CeylonIdePlugin.embeddedCeylonRepository;
        value modulePaths = ArrayList<Str>();
        modulePaths.add(str(CeylonIdePlugin.classesDir.absolutePath));
        FileUtil.visitFiles(repo, (file) {
            if (file.file && file.name.endsWith(".jar")) {
                modulePaths.add(str(file.absolutePath));
            }
            return true;
        });
        return modulePaths;
    }

    shared actual late List<Str> classPath = compute();

}
