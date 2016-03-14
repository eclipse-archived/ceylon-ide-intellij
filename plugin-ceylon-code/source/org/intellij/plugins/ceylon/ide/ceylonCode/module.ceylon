"Default documentation for module `org.intellij.plugins.ceylon.ide.ceylonCode`."

native("jvm")
module org.intellij.plugins.ceylon.ide.ceylonCode "1.2.0" {
    shared import java.base "7";
    shared import com.redhat.ceylon.ide.common "1.2.3";
    shared import com.intellij.openapi "current";
    shared import com.intellij.idea "current";
    import com.github.rjeschke.txtmark "0.13";
    shared import java.desktop "7";
    import java.compiler "7";
}
