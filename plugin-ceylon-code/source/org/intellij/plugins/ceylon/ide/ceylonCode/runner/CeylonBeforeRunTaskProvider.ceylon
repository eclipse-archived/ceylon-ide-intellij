import ceylon.interop.java {
    CeylonStringIterable
}

import com.intellij.execution {
    BeforeRunTaskProvider,
    BeforeRunTask,
    CommonProgramRunConfigurationParameters,
    RunContentExecutor
}
import com.intellij.execution.configurations {
    RunConfiguration,
    ModuleBasedConfiguration,
    RunConfigurationModule,
    GeneralCommandLine
}
import com.intellij.execution.process {
    OSProcessHandler,
    ProcessAdapter,
    ProcessEvent
}
import com.intellij.execution.runners {
    ExecutionEnvironment
}
import com.intellij.openapi.actionSystem {
    DataContext
}
import com.intellij.openapi.application {
    ApplicationManager {
        application
    }
}
import com.intellij.openapi.compiler {
    CompilerManager,
    CompileContext
}
import com.intellij.openapi.diagnostic {
    Logger
}
import com.intellij.openapi.extensions {
    Extensions
}
import com.intellij.openapi.project {
    Project
}
import com.intellij.openapi.util {
    Key,
    Ref
}
import com.intellij.util.concurrency {
    Semaphore
}
import com.intellij.util.execution {
    ParametersListUtil
}

import java.io {
    File
}

import org.intellij.plugins.ceylon.ide.ceylonCode {
    ITypeCheckerInvoker
}
import org.intellij.plugins.ceylon.ide.ceylonCode.model {
    getCeylonProject
}
import org.intellij.plugins.ceylon.ide.ceylonCode.util {
    icons
}
import org.jdom {
    Element
}

shared class CeylonBeforeRunTaskProvider extends BeforeRunTaskProvider<CeylonBeforeRunTask> {

    shared static Key<CeylonBeforeRunTask> key = Key.create<CeylonBeforeRunTask>("Ceylon");

    Project project;
    shared new (Project project) extends BeforeRunTaskProvider<CeylonBeforeRunTask>() {
        this.project = project;
    }

    canExecuteTask(RunConfiguration configuration, CeylonBeforeRunTask task)
            => task.command.size > 0;

    configurable => true;

    shared actual Boolean configureTask(RunConfiguration runConfiguration, CeylonBeforeRunTask task) {
        value dialog = CeylonBeforeRunEditor(runConfiguration.project);
        dialog.loadTask(task);
        dialog.title = "Configure Ceylon command";

        if (dialog.showAndGet()) {
            dialog.updateTask(task);
        }
        return true;
    }

    createTask(RunConfiguration runConfiguration)
            => CeylonBeforeRunTask();

    function executeTaskInternal(RunConfiguration configuration, CeylonBeforeRunTask task) {
        value workingDirectory = if (is CommonProgramRunConfigurationParameters configuration)
            then File(configuration.workingDirectory)
            else if (is ModuleBasedConfiguration<out RunConfigurationModule> configuration,
                     exists mod = configuration.configurationModule.\imodule)
            then getCeylonProject(mod)?.rootDirectory
            else null;

        if (exists workingDirectory,
            exists tcInvoker = Extensions.getExtensions(ITypeCheckerInvoker.epName).get(0)) {

            try {
                value ceylonBinary = File(tcInvoker.embeddedCeylonDist, "bin/ceylon");
                value command = GeneralCommandLine(ceylonBinary.absolutePath, task.command, *task.parameters)
                    .withWorkDirectory(workingDirectory);
                value processHandler = OSProcessHandler(command);
                value executor = RunContentExecutor(configuration.project, processHandler)
                    .withTitle("ceylon swarm");

                value exitCode = Ref<Integer>();
                processHandler.addProcessListener(object extends ProcessAdapter() {
                    processTerminated(ProcessEvent event) => exitCode.set(event.exitCode);
                });

                application.invokeLater(executor.run);

                processHandler.waitFor();
                if (exists code = exitCode.get(),
                    code == 0) {
                    return true;
                }
            } catch (e) {
                Logger.getInstance(`class`.qualifiedName).error("Could not run " + task.string, e);
            }
            return false;
        }
        return true; // TODO should be false, we shouldn't fail silently
    }

    shared actual Boolean executeTask(DataContext context, RunConfiguration configuration,
        ExecutionEnvironment env, CeylonBeforeRunTask task) {

        value compilerManager = CompilerManager.getInstance(env.project);
        value compileScope = compilerManager.createProjectCompileScope(env.project);
        value done = Semaphore();
        done.down();

        void callback(Boolean aborted, Integer errors, Integer warnings, CompileContext ctx) {
            done.up();
        }

        application.invokeLater(() => compilerManager.make(compileScope, callback));

        done.waitFor();

        return executeTaskInternal(configuration, task);
    }

    getDescription(CeylonBeforeRunTask task) => "Run 'ceylon ``task.command``'";

    id => key;

    name => "Run Ceylon command";

    icon => icons.ceylon;

    getTaskIcon(CeylonBeforeRunTask task) => icon;

}

shared class CeylonBeforeRunTask(command = "", parameters = {})
        extends BeforeRunTask<CeylonBeforeRunTask>(CeylonBeforeRunTaskProvider.key) {

    shared variable String command;
    shared variable {String*} parameters;

    shared actual void readExternal(Element element) {
        super.readExternal(element);
        command = element.getAttributeValue("command");
        value rawParams = element.getAttributeValue("parameters");
        parameters = CeylonStringIterable(ParametersListUtil.parse(rawParams));
    }

    shared actual void writeExternal(Element element) {
        super.writeExternal(element);
        element.setAttribute("command", command);
        value rawParams = ParametersListUtil.join(*parameters);
        element.setAttribute("parameters", rawParams);
    }

    shared actual Boolean equals(Object that) {
        if (is CeylonBeforeRunTask that) {
            return command==that.command &&
                parameters.sequence()==that.parameters.sequence();
        }
        else {
            return false;
        }
    }

    shared actual Integer hash {
        variable value hash = 1;
        hash = 31*hash + command.hash;
        hash = 31*hash + parameters.hash;
        return hash;
    }

    string => "CeylonBeforeRunTask[ceylon ``command`` ``parameters``]";
}
