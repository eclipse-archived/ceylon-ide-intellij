import ceylon.language {Integer}
import <error descr="package not found in imported modules: ceylon.languagexyz (define a module and add module import to its module descriptor)">ceylon.languagexyz</error> {Integer}

shared class Hello() {
    void sayHello() {
        <error descr="function or value does not exist: a">a</error> = 3;
    }
}