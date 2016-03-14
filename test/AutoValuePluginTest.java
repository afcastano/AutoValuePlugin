import com.afcastano.intellij.autovalue.actions.AddMissingMethodsToBuilderAction;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NonNls;
import org.junit.Ignore;

public class AutoValuePluginTest extends LightCodeInsightFixtureTestCase {

    @NonNls
    private static final String AUTOVALUE = "com/google/auto/value/AutoValue.java";
    private static final String AUTOPARCEL = "auto/parcel/AutoParcel.java";
    private static final String AUTOPARCEL_GSON = "auto/parcelgson/AutoParcelGson.java";

    @Override
    protected void setUp() throws Exception {
        VfsRootAccess.SHOULD_PERFORM_ACCESS_CHECK = false; // TODO: a workaround for v15
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    public void testSimpleClass() {

        myFixture.configureByFiles("generatebuilder/basic/BasicTestFile.java", AUTOVALUE);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/basic/BasicTestFile_expected.java", true);

    }

    public void testNestedClasses() {

        myFixture.configureByFiles("generatebuilder/nested/NestedClasses.java", AUTOVALUE);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/nested/NestedClasses_expected.java", true);

    }

    public void testNonJavaFile() {

        myFixture.configureByFiles("generatebuilder/nonJava/test.js");
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/nonJava/test.js", true);

    }

    public void testNotAnnotated() {

        myFixture.configureByFiles("generatebuilder/notannotated/NotAnnotated.java");
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/notannotated/NotAnnotated.java", true);

    }

    @Ignore
    public void testNotAbstract() {
// TODO Add check to avoid non abstract java classes.
//        myFixture.configureByFiles("generatebuilder/notabstract/Test.java", "com/google/auto/value/AutoValue.java");
//        myFixture.testAction(new AddMissingMethodsToBuilderAction());
//        myFixture.checkResultByFile("generatebuilder/notabstract/Test.java", true);

    }

    public void testAddNewProperty() {
        myFixture.configureByFiles("generatebuilder/addnewproperty/Test.java", AUTOVALUE);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/addnewproperty/Test_expected.java", true);

    }

    public void testAddBuilderFactory() {
        myFixture.configureByFiles("generatebuilder/addbuilderfactory/Test.java", AUTOVALUE);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/addbuilderfactory/Test_expected.java", true);

    }

    public void testJavaBeanStyle() {
        myFixture.configureByFiles("generatebuilder/javabeanstyle/Test.java", AUTOVALUE);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/javabeanstyle/Test_expected.java", true);

    }


    public void testBasicAutoParcel() {
        myFixture.configureByFiles("generatebuilder/basicautoparcel/Test.java", AUTOPARCEL);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/basicautoparcel/Test_expected.java", true);

    }

    public void testBasicAutoParcelGson() {
        myFixture.configureByFiles("generatebuilder/basicautoparcelgson/Test.java", AUTOPARCEL_GSON);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile("generatebuilder/basicautoparcelgson/Test_expected.java", true);

    }

}
