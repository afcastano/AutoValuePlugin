import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String getValue();

    public abstract Boolean isValue2();

    public abstract int nonJavaBean();

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder getValue(String getValue);

        public abstract Builder isValue2(Boolean isValue2);

        public abstract Builder nonJavaBean(int nonJavaBean);

        public abstract BasicTestFile build();
    }
}