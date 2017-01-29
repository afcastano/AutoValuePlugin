import com.google.auto.value.AutoValue;
import test.i2.Interface2;
import test.i3.Interface3;

@AutoValue
public abstract class BasicTestFile implements Interface2, Interface3 {


    public abstract String value();

    public int method1Interface2() {
        return 3;
    }

    public Integer notAbstract() {
        return 0;
    }

    public static BasicTestFile create(int method1Interface1, int method2Interface1, int method2Interface2, int method1Interface3, int method2Interface3, String value) {
        return new AutoValue_BasicTestFile(method1Interface1, method2Interface1, method2Interface2, method1Interface3, method2Interface3, value);
    }

}