import org.intellij.plugins.ceylon.ide.ceylonCode.correct {
    RefineFormalMembersIntention
}

shared class RefineFormalMembersAction() extends AbstractIntentionAction() {
    
    commandName => "Refine formal members";
    
    createIntention() => RefineFormalMembersIntention();
}
