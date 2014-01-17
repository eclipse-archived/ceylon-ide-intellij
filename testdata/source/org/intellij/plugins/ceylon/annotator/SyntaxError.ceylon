import ceylon.language {Integer}
import <error descr="package not found in imported modules: ceylon.languagexyz">ceylon.languagexyz</error> {Integer}

shared class Hello() {
    void sayHello() {
        <error descr="function or value does not exist: a">a</error> = 3;
    }
}