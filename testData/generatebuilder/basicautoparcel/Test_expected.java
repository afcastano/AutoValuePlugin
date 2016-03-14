import auto.parcel.AutoParcel;

@AutoParcel
public abstract class BasicTestFile {


    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

    public static Builder builder() {
        return new AutoParcel_BasicTestFile.Builder();
    }

    @AutoParcel.Builder
    public abstract static class Builder {
        public abstract Builder value(String value);

        public abstract BasicTestFile build();
    }
}