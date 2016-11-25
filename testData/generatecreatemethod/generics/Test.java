import com.google.auto.value.AutoValue;

@AutoValue
public abstract class GenericsTestFile<T1, T2> {
    <caret>
    public abstract T1 value();
    public abstract T2 value2();
}