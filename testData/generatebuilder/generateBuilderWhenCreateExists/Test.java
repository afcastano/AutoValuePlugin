import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {
    <caret>
    public abstract String value();
    public abstract Integer value2();
    public abstract Boolean value3();

    public static BasicTestFile create(String value, Integer value2, Boolean value3) {
        return new AutoValue_BasicTestFile(value, value2, value3);
    }

}