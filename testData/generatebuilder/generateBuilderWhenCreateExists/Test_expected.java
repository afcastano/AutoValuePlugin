import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {

    public abstract String value();
    public abstract Integer value2();
    public abstract Boolean value3();

    public static BasicTestFile create(String value, Integer value2, Boolean value3) {
        return builder()
                .value(value)
                .value2(value2)
                .value3(value3)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract Builder value2(Integer value2);

        public abstract Builder value3(Boolean value3);

        public abstract BasicTestFile build();
    }
}