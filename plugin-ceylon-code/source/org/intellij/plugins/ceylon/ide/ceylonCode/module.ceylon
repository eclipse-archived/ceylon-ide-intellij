"Default documentation for module `org.intellij.plugins.ceylon.ide.ceylonCode`."

native("jvm")
module org.intellij.plugins.ceylon.ide.ceylonCode "current" {
    shared import java.base "7";
    shared import com.redhat.ceylon.ide.common "1.3.1";
    shared import com.intellij.openapi "current";
    shared import com.intellij.idea "current";
    shared import org.jetbrains.plugins.gradle "current";
    shared import org.intellij.groovy "current";
    import com.github.rjeschke.txtmark "0.13";
    shared import java.desktop "7";
    import java.compiler "7";
}
