import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {
    <caret>

    public abstract String value();

    public abstract Integer value2();

    //Checks the return type
    public abstract Builder toBuilder();

}