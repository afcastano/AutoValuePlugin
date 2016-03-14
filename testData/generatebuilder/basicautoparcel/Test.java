import auto.parcel.AutoParcel;

@AutoParcel
public abstract class BasicTestFile {

    <caret>
    public abstract String value();

    public Integer notAbstract() {
        return 0;
    }

}