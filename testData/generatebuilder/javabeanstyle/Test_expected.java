import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String getValue();

    public abstract Boolean isValue2();

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder setValue(String newValue);

        public abstract Builder setValue2(Boolean newValue2);

        public abstract BasicTestFile build();
    }
}