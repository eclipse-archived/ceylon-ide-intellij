import com.intellij.compiler.server {
    BuildProcessParametersProvider
}
import com.intellij.openapi.util.io {
    FileUtil
}
import com.intellij.util {
    CommonProcessors
}
import org.intellij.plugins.ceylon.ide.startup {
    CeylonIdePlugin
}
import org.jetbrains.annotations {
    NotNull
}
import java.io {
    File
}
import java.util {
    ArrayList,
    List
}
import java.lang {
    Str=String,
    Types {
        str=nativeString
    }
}

shared class BuildClasspathProvider() extends BuildProcessParametersProvider() {

    function compute() {
        value repo = CeylonIdePlugin.embeddedCeylonRepository;
        value modulePaths = ArrayList<Str>();
        modulePaths.add(str(CeylonIdePlugin.classesDir.absolutePath));
        FileUtil.visitFiles(repo, (File file) {
            if (file.file, file.name.endsWith(".jar")) {
                modulePaths.add(str(file.absolutePath));
            }
            return false;
        });
        return modulePaths;
    }

    shared actual late List<Str> classPath = compute();

}
