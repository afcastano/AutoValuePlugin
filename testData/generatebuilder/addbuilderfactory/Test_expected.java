import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract BasicTestFile build();
    }
}