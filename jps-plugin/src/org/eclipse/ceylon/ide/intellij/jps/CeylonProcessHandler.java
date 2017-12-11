package org.eclipse.ceylon.ide.intellij.jps;

import com.intellij.execution.process.BaseOSProcessHandler;
import com.intellij.execution.process.ProcessOutputTypes;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.util.Consumer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.nio.charset.Charset;

/**
 * TODO
 */
public class CeylonProcessHandler extends BaseOSProcessHandler {

    @NotNull
    private final Consumer<String> statusUpdater;
    private final StringBuffer stdErr = new StringBuffer();

    public CeylonProcessHandler(@NotNull Process process, @NotNull Consumer<String> statusUpdater) {
        super(process, null, null);
        this.statusUpdater = statusUpdater;
    }

    @Override
    public void notifyTextAvailable(String text, Key outputType) {
        super.notifyTextAvailable(text, outputType);

        if (outputType == ProcessOutputTypes.SYSTEM) {
            return;
        }

        if (outputType == ProcessOutputTypes.STDERR) {
            stdErr.append(StringUtil.convertLineSeparators(text));
            return;
        }

        statusUpdater.consume(text);
    }

    public String getStdErr() {
        return stdErr.toString();
    }
}
