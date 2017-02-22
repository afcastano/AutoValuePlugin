import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String value();

    public abstract Integer value2();

    public abstract Something value3();

    public abstract int value4();

    //Checks the return type
    public abstract Builder toBuilders();

    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract Builder value2(Integer value2);

        public abstract Builder value3(Something value3);

        public abstract Builder value4(int value4);

        public abstract BasicTestFile build();
    }
}