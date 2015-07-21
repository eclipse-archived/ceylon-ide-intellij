package org.intellij.plugins.ceylon.ide.validate;

import org.junit.Test;

import static org.intellij.plugins.ceylon.ide.validate.NameValidator.packageNameIsLegal;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;

public class NameValidatorTest {

    @Test
    public void testPackageNameIsLegal() {
        // invalid package names
        assertFalse(packageNameIsLegal("!"));
        assertFalse(packageNameIsLegal("%"));
        assertFalse(packageNameIsLegal("@^&Module"));
        assertFalse(packageNameIsLegal("-="));
        assertFalse(packageNameIsLegal("+++"));
        assertFalse(packageNameIsLegal("a\\b"));
        assertFalse(packageNameIsLegal("a/b"));
        assertFalse(packageNameIsLegal("A$B"));
        assertFalse(packageNameIsLegal("()"));
        assertFalse(packageNameIsLegal("1"));
        assertFalse(packageNameIsLegal("1a"));
        assertFalse(packageNameIsLegal("My Module"));
        assertFalse(packageNameIsLegal("1_2"));
        assertFalse(packageNameIsLegal("A"));
        assertFalse(packageNameIsLegal("A-B"));
        assertFalse(packageNameIsLegal("A (B)"));
        assertFalse(packageNameIsLegal("A-1_000-1"));
        assertFalse(packageNameIsLegal("MyModule"));
        assertFalse(packageNameIsLegal("Ax1"));
        assertFalse(packageNameIsLegal(".Ax1"));
        assertFalse(packageNameIsLegal(".ax.e"));
        assertFalse(packageNameIsLegal("a..e"));
        assertFalse(packageNameIsLegal("ax.ed."));
        assertFalse(packageNameIsLegal("module"));
        assertFalse(packageNameIsLegal("my.module"));
        assertFalse(packageNameIsLegal("my.module.a"));
        assertFalse(packageNameIsLegal("function"));
        assertFalse(packageNameIsLegal("class"));
        assertFalse(packageNameIsLegal("package"));
        assertFalse(packageNameIsLegal("interface"));
        assertFalse(packageNameIsLegal("void"));

        // valid package names

        // empty String considered legal but IDE should consider the field as *required
        assertTrue(packageNameIsLegal(""));
        assertTrue(packageNameIsLegal("_"));
        assertTrue(packageNameIsLegal("a"));
        assertTrue(packageNameIsLegal("_1"));
        assertTrue(packageNameIsLegal("_a"));
        assertTrue(packageNameIsLegal("myModule"));
        assertTrue(packageNameIsLegal("my.mod.ul.e"));
        assertTrue(packageNameIsLegal("myModuleWhichIsReallyCool"));
        assertTrue(packageNameIsLegal("v0"));
    }

}
