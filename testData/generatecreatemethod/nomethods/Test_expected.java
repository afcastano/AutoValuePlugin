import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public static Builder builder() {
        return new AutoValue_BasicTestFile.Builder();
    }

    public static BasicTestFile create() {
        return builder()
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract BasicTestFile build();
    }
}