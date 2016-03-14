import com.google.auto.value.AutoValue;

@AutoValue
public class BasicTestFile {

    <caret>
    public Integer notAbstract() {
        return 0;
    }

}