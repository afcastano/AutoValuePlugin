import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public abstract String value();

    public abstract int another();

    public Integer notAbstract() {
        return 0;
    }

    public static BasicTestFile create(String value, int another) {
        return new AutoValue_BasicTestFile(value, another);
    }

}