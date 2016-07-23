import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public abstract int another();

    public Integer notAbstract() {
        return 0;
    }

    public static BasicTestFile create(String value) {
        return builder()
                .value(value)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract Builder another(int another);

        public abstract BasicTestFile build();
    }

}