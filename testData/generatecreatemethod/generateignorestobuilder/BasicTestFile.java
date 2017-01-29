import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public abstract int another();

    public Integer notAbstract() {
        return 0;
    }

    public abstract Builder toBuilder();

}