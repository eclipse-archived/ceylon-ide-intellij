doc("this is a doc for goodbye")
void goodbye() {

}
doc("this is a *doc* for _hello_ world

      * a list
      * with bullets

     # And a header #

     with text below

     > a simple quote with `some code inside`

         public class Foo {
             hello();
         }

     [Ceylon-IDEA](https://github.com/ceylon/ceylon-ide-intellij) is great.
     " )
throws(`class Exception`)
shared void helloAgain() {
    print("Hello, beautiful world!");
}

doc("this is a doc for MyClass")
see(`class Integer`)
throws(`class Exception`, "sometimes")
class MyClass() {

}

doc("this is an interface")
by("Bastien")
interface MyInterface {
	
}

//"this is a declaration without definition"
//by("Bastien")
//class MyClassDeclaration;