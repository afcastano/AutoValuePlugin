import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public abstract int another();

    public Integer notAbstract() {
        return 0;
    }

    //Checks the return type
    public abstract Builder toBuilderAny();

}