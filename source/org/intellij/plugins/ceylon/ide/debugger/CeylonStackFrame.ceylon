import ceylon.collection {
    ArrayList
}

import com.intellij.debugger.engine {
    JavaStackFrame
}
import com.intellij.debugger.jdi {
    LocalVariableProxyImpl
}
import com.intellij.debugger.ui.impl.watch {
    StackFrameDescriptorImpl
}
import com.redhat.ceylon.common {
    JVMModuleUtil
}

import java.util {
    List
}
import java.util.regex {
    Pattern
}

class CeylonStackFrame(StackFrameDescriptorImpl descriptor, Boolean update)
        extends JavaStackFrame(descriptor, update) {


    shared actual List<LocalVariableProxyImpl> visibleVariables {
        value visibleVariables = super.visibleVariables;
        value it = visibleVariables.iterator();
        value processedNames = ArrayList<String>();

        while (it.hasNext()) {
            value var = it.next();
            value name = fixVariableName(var.name(), true, false);

            if (name.contains("$")) {
                it.remove();
            }
            if (processedNames.contains(name)) {
                it.remove();
            } else {
                processedNames.add(name);
            }
        }

        return visibleVariables;
    }
}

Pattern localVariablePattern = Pattern.compile("([^$]+)\\$[0-9]+");

String fixVariableName(variable String name, Boolean isLocalVariable, Boolean isSynthetic) {
    if (isSynthetic, name.startsWith("val$")) {
        name = name.removeInitial("val$");
    }
    if (exists c = name.first,
        c == '$') {
        if (JVMModuleUtil.isJavaKeyword(name, 1, name.size)) {
            name = name.substring(1);
        }
    }

    if (isLocalVariable || isSynthetic,
        name.contains("$")) {

        if (name.endsWith("$param$")) {
            return name.substring(0, name.size - "$param$".size);
        }
        value matcher = localVariablePattern.matcher(name);
        if (matcher.matches()) {
            name = matcher.group(1);
        }
    }
    return name;
}


