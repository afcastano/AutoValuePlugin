import com.google.auto.value.AutoValue;

@AutoValue
public abstract class BasicTestFile {


    public static BasicTestFile create() {
        return new AutoValue_BasicTestFile();
    }
}