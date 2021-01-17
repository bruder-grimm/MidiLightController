package util;

public class Tuple<A, B> {
    public final A _1;
    public final B _2;

    private Tuple(A _1, B _2) {
        this._1 = _1;
        this._2 = _2;
    }

    public static <A, B> Tuple<A, B> apply(A _1, B _2) {
        return new Tuple<>(_1, _2);
    }

    @Override
    public String toString() {
        return "Tuple{" +
                "_1=" + _1 +
                ", _2=" + _2 +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple<?, ?> tuple = (Tuple<?, ?>) o;

        if (_1 != null ? !_1.equals(tuple._1) : tuple._1 != null) return false;
        return _2 != null ? _2.equals(tuple._2) : tuple._2 == null;
    }

    @Override public int hashCode() {
        int result = _1 != null ? _1.hashCode() : 0;
        result = 31 * result + (_2 != null ? _2.hashCode() : 0);
        return result;
    }
}
