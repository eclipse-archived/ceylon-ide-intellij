import org.intellij.plugins.ceylon.ide.correct {
    RefineEqualsHashIntention
}

shared class RefineEqualsHashAction() extends AbstractIntentionAction() {

    commandName => "Refine equals()/hash";
    
    createIntention() => RefineEqualsHashIntention();
    
}
