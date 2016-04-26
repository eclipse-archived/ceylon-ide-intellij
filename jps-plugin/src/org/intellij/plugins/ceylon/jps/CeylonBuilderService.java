package org.intellij.plugins.ceylon.jps;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.jps.incremental.BuilderService;
import org.jetbrains.jps.incremental.ModuleLevelBuilder;

import java.util.Collections;
import java.util.List;

public class CeylonBuilderService extends BuilderService {

    @NotNull
    @Override
    public List<? extends ModuleLevelBuilder> createModuleLevelBuilders() {
        return Collections.singletonList(new CeylonBuilder());
    }

}