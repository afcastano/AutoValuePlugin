import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract BasicTestFile build();
    }
}