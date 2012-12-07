import ceylon.math.integer { ... }

abstract class Compute<T>() {
    formal shared T op(T param1, T param2);
}

class Smallest() extends Compute<Integer>() {
    Integer bar;

    interface Foo {}

    actual shared Integer op(Integer param1, Integer param2) {
        return smallest(param1, param2);
    }
}