import com.google.auto.value.AutoValue;
import test.i2.Interface2;
import test.i3.Interface3;

@AutoValue
public abstract class BasicTestFile implements Interface2, Interface3 {

    <caret>
    public abstract String value();

    public int method1Interface2() {
        return 3;
    }

    public Integer notAbstract() {
        return 0;
    }

}