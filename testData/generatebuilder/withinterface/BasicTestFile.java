import com.google.auto.value.AutoValue;
import test.Interface1;

@AutoValue
public abstract class BasicTestFile implements Interface1 {

    <caret>
    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

}