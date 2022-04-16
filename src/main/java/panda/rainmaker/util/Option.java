package panda.rainmaker.util;

public class Option<T> {
    private final T value;

    public Option(T value) {
        this.value = value;
    }

    public T getValue() {
        return value;
    }
}
