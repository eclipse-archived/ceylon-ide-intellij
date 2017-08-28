native("jvm")
module org.intellij.plugins.ceylon.ide "current" {
    shared import java.base "7";
    shared import com.redhat.ceylon.ide.common "1.3.4-SNAPSHOT";
    shared import ceylon.tool.converter.java2ceylon "1.3.4-SNAPSHOT";
    shared import com.intellij.openapi "current";
    shared import com.intellij.idea "current";
    shared import org.jetbrains.plugins.gradle "current";
    shared import org.intellij.groovy "current";
    shared import org.intellij.maven "current";
    shared import org.jdom "current";
    shared import maven:com.google.guava:"guava" "19.0";
    import maven:com.intellij:"forms_rt" "7.0.3";
    import maven:"commons-lang":"commons-lang" "2.6";
    import com.github.rjeschke.txtmark "0.13";
    shared import java.desktop "7";
    import java.compiler "7";
    shared import jdk.tools "current";
}
