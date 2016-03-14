import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {
    <caret>

    public abstract String getValue();

    public abstract Boolean isValue2();

}