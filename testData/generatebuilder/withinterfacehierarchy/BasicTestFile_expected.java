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

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder method1Interface1(int method1Interface1);

        public abstract Builder method2Interface1(int method2Interface1);

        public abstract Builder method2Interface2(int method2Interface2);

        public abstract Builder method1Interface3(int method1Interface3);

        public abstract Builder method2Interface3(int method2Interface3);

        public abstract Builder value(String value);

        public abstract BasicTestFile build();
    }
}