"Default documentation for module `org.intellij.plugins.ceylon.ide.ceylonCode`."

native("jvm")
module org.intellij.plugins.ceylon.ide.ceylonCode "current" {
    shared import java.base "7";
    shared import com.redhat.ceylon.ide.common "1.3.0";
    shared import com.intellij.openapi "current";
    shared import com.intellij.idea "current";
    shared import org.jetbrains.plugins.gradle "current";
    shared import org.intellij.groovy "current";
    import com.github.rjeschke.txtmark "0.13";
    shared import java.desktop "7";
    import java.compiler "7";

    import com.intellij.util "current";

    import com.vasileff.ceylon.dart.compiler "1.3.0-DP3";

    // JV: Adding these transitive dependencies to make sure they show up in
    // plugin-ceylon-code.iml? Not all seem to be necessary though... for
    // example, ceylon.collection appears in the iml regardless. Maybe
    // only exported transitive dependencies show up?
    import ceylon.collection "1.3.0";
    import ceylon.interop.java "1.3.0";
    import ceylon.ast.core "1.3.0";
    import ceylon.ast.create "1.3.0";
    import ceylon.ast.redhat "1.3.0";
    import ceylon.whole "1.3.0";
    import ceylon.json "1.3.0";
    import ceylon.file "1.3.0";
    import ceylon.buffer "1.3.0";
    import com.vasileff.ceylon.structures "1.0.0";
}
