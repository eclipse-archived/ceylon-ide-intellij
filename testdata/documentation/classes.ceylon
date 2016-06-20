"This is the simplest class possible"
class Foo() {
}

"This is an interface"
interface Bar {
}

interface Baz {
}

"A class that does things"
see(`class Foo`)
by("Someone", "Another one")
class Woot() extends Foo() satisfies Bar & Baz {
}