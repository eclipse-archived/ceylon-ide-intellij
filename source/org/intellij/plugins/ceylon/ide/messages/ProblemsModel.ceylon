import ceylon.collection {
    HashMap
}

import com.redhat.ceylon.ide.common.model {
    Severity
}

import org.intellij.plugins.ceylon.ide.model {
    IdeaCeylonProject
}

class ProblemsModel() {
    value problemsByProject = HashMap<IdeaCeylonProject, Problems>();

    class Problems(project, frontendMessages, backendMessages, projectMessages) {

        IdeaCeylonProject project;

        {SourceMsg*}? frontendMessages;
        {SourceMsg*}? backendMessages;
        {ProjectMsg*}? projectMessages;

        shared Integer count(Severity s)
            => [frontendMessages, backendMessages, projectMessages].coalesced.fold(0,
                (initial, messages)
                        => initial + messages.count((msg) => msg.severity == s));

        shared Integer countWarnings() => count(Severity.warning);

        shared Integer countErrors() => count(Severity.error);

        shared {BuildMsg*} allMessages
                => expand([frontendMessages, backendMessages, projectMessages].coalesced);
    }

    shared void updateProblems(IdeaCeylonProject project, {SourceMsg*}? frontendMessages,
        {SourceMsg*}? backendMessages, {ProjectMsg*}? projectMessages) {

        problemsByProject[project]
            = Problems {
                project = project;
                frontendMessages = frontendMessages;
                backendMessages = backendMessages;
                projectMessages = projectMessages;
            };
    }

    shared Integer count(Severity s)
            => problemsByProject.items.fold(0, (sum, item) => sum + item.count(s));

    shared Integer countWarnings()
            => problemsByProject.items.fold(0, (sum, item) => sum + item.countWarnings());

    shared Integer countErrors()
            => problemsByProject.items.fold(0, (sum, item) => sum + item.countErrors());

    shared {BuildMsg*} allMessages => expand(problemsByProject.items.map(Problems.allMessages));

    shared void clear() {
        problemsByProject.clear();
    }
}
