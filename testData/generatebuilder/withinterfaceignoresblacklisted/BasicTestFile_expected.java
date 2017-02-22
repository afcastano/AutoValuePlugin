import com.google.auto.value.AutoValue;
import test.Interface1;
import android.os.Parcelable;
import java.util.Map;

@AutoValue
public abstract class BasicTestFile implements Interface1, Parcelable, Map<String, String> {


    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder method1Interface1(int method1Interface1);

        public abstract Builder method2Interface1(int method2Interface1);

        public abstract Builder value(String value);

        public abstract BasicTestFile build();
    }
}