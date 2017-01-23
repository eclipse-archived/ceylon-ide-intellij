import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    RefineEqualsHashIntention
}

shared class RefineEqualsHashAction() extends AbstractIntentionAction() {

    commandName => "Refine equals()/hash";
    
    createIntention() => RefineEqualsHashIntention();
    
}
