import auto.parcelgson.AutoParcelGson;

@AutoParcelGson
public abstract class BasicTestFile {


    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

    public static Builder builder() {
        return new AutoParcelGson_BasicTestFile.Builder();
    }

    @AutoParcelGson.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract BasicTestFile build();
    }
}