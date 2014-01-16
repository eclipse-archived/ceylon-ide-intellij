shared class Hello() {
    void sayHello() {
        <error descr="function or value does not exist: a">a</error> = 3;
    }
}