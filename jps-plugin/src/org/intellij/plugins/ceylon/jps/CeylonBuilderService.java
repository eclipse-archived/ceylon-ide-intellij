package org.intellij.plugins.ceylon.jps;

import com.redhat.ceylon.compiler.java.runtime.tools.Backend;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.BuilderCategory;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;

import java.util.Arrays;
import java.util.List;

public class CeylonBuilderService extends BuilderService {

    @NotNull
    @Override
    public List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
        return Arrays.asList(
                // We need to call the JVM backend before the Java compiler
                new CeylonBuilder(Backend.Java, BuilderCategory.SOURCE_PROCESSOR),
                new CeylonBuilder(Backend.JavaScript, BuilderCategory.TRANSLATOR)
        );
    }
}
