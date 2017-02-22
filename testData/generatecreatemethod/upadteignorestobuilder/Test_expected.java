import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String value();

    public abstract int another();

    public Integer notAbstract() {
        return 0;
    }

    public abstract bool newProp();

    public abstract Builder toBuilder();

    public static BasicTestFile create(String value, int another, bool newProp) {
        return builder()
                .value(value)
                .another(another)
                .newProp(newProp)
                .build();
    }

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract Builder another(int another);

        public abstract Builder newProp(bool newProp);

        public abstract BasicTestFile build();
    }

}