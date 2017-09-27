import org.eclipse.ceylon.ide.intellij.correct {
    RefineEqualsHashIntention
}

shared class RefineEqualsHashAction() extends AbstractIntentionAction() {

    commandName => "Refine equals()/hash";
    
    createIntention() => RefineEqualsHashIntention();
    
}
