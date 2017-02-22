import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GenericsTestFile<T1, T2> {
    <caret>
    public abstract T1 value();
    public abstract T2 value2();

    public static <T1, T2> Builder<T1, T2> builder() {
        return new AutoValue_GenericsTestFile.Builder<>();
    }

    @AutoValue.Builder
    public abstract static class Builder<T1, T2> {
        public abstract Builder<T1, T2> value(T1 value);

        public abstract Builder<T1, T2> value2(T2 value2);

        public abstract GenericsTestFile<T1, T2> build();
    }
}