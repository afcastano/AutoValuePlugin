import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GenericsTestFile<T1, T2> {

    public abstract T1 value();
    public abstract T2 value2();

    public static <T1, T2> GenericsTestFile<T1, T2> create(T1 value, T2 value2) {
        return new AutoValue_GenericsTestFile<>(value, value2);
    }
}