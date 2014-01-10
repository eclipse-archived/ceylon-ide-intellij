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
}

"documented plop"
void plop() {
    // Not the same as BarBar#plop :)
}

class BarBar() {

    shared void plop() {
        myHelloWorld();
    }
}
