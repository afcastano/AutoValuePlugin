import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

}