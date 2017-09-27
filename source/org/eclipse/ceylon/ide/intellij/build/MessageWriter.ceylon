import com.intellij.openapi.compiler {
    CompileContext,
    CompilerMessageCategory
}

import java.io {
    Writer
}
import java.lang {
    CharArray,
    Str=String
}

shared class MessageWriter(CompileContext context) extends Writer() {

    write(CharArray cbuf, Integer off, Integer len)
            => context.addMessage(CompilerMessageCategory.information,
                    Str(cbuf, off, len).string, null, -1, -1);

    shared actual void flush() {}

    shared actual void close() {}

}
