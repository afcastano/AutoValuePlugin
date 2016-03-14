import auto.parcelgson.AutoParcelGson;

@AutoParcelGson
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

}