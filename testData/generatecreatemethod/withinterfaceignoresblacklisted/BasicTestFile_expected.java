import com.google.auto.value.AutoValue;
import test.Interface1;
import android.os.Parcelable;
import java.util.Map;

@AutoValue
public abstract class BasicTestFile implements Interface1, Parcelable, Map<String, String> {


    public abstract String value();

    public int method1Interface2() {
        return 3;
    }

    public Integer notAbstract() {
        return 0;
    }

    public static BasicTestFile create(int method1Interface1, int method2Interface1, String value) {
        return new AutoValue_BasicTestFile(method1Interface1, method2Interface1, value);
    }

}