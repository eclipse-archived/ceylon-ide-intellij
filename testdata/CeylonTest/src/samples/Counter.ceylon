package my.packagename;

class CounterNopeMan(Integer initialValue=0) {

    @type["J4&J2"] variable value count := initialValue;

    shared Integer currentValue {
        return count;
    }

    shared void increment() {
        count++;
    }

}
