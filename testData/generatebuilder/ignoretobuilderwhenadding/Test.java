import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {
    <caret>

    public abstract String value();

    public abstract Integer value2();

    public abstract Something value3();

    public abstract int value4();

    public abstract Builder toBuilder();

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract Builder value2(Integer value2);

        public abstract BasicTestFile build();
    }
}