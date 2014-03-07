import ceylon.language {Integer}

// This code has no purpose other than testing references :)
void myHelloWorld() {
    Integer foo;

    "funny noise"
    String prout;

    BarBar bar2 = BarBar();

    prout = "test";

    print("hello");
    print(prout.size);

    bar2.plop();

    myHelloWorld();

    plop();

    print(bar2.attr);
}

"documented plop"
void plop() {
    // Not the same as BarBar#plop :)
}

class BarBar(shared Integer attr = 4) {

    shared void plop() {
        myHelloWorld();
    }
}

void methodWithParam(Integer par) {
    print(par);
}