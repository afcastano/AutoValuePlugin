import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {
    <caret>

    public abstract String value();

    public abstract Integer value2();

    public abstract Builder toBuilder();

}