import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

    public abstract bool newProp();

    public static BasicTestFile create(String value, bool newProp) {
        return builder()
                .value(value)
                .newProp(newProp)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract Builder newProp(bool newProp);

        public abstract BasicTestFile build();
    }

}