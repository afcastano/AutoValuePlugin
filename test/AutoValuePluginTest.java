import com.afcastano.intellij.autovalue.actions.AddMissingMethodsToBuilderAction;
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NonNls;

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

        runAddMissingMethodsTest("generatebuilder/basic/BasicTestFile.java",
                "generatebuilder/basic/BasicTestFile_expected.java",
                AUTOVALUE);

    }

    public void testNestedClasses() {

        runAddMissingMethodsTest("generatebuilder/nested/NestedClasses.java",
                "generatebuilder/nested/NestedClasses_expected.java",
                AUTOVALUE);

    }

    public void testNonJavaFile() {

        runAddMissingMethodsTest("generatebuilder/nonJava/test.js",
                "generatebuilder/nonJava/test.js",
                AUTOVALUE);

    }

    public void testNotAnnotated() {

        runAddMissingMethodsTest("generatebuilder/notannotated/NotAnnotated.java",
                "generatebuilder/notannotated/NotAnnotated.java",
                AUTOVALUE);

    }

    public void testAddNewProperty() {
        runAddMissingMethodsTest("generatebuilder/addnewproperty/Test.java",
                "generatebuilder/addnewproperty/Test_expected.java",
                AUTOVALUE);

    }

    public void testAddBuilderFactory() {
        runAddMissingMethodsTest("generatebuilder/addbuilderfactory/Test.java",
                "generatebuilder/addbuilderfactory/Test_expected.java",
                AUTOVALUE);

    }

    public void testJavaBeanStyle() {
        runAddMissingMethodsTest("generatebuilder/javabeanstyle/Test.java",
                "generatebuilder/javabeanstyle/Test_expected.java",
                AUTOVALUE);

    }


    public void testBasicAutoParcel() {
        runAddMissingMethodsTest("generatebuilder/basicautoparcel/Test.java",
                "generatebuilder/basicautoparcel/Test_expected.java",
                AUTOPARCEL);

    }

    public void testBasicAutoParcelGson() {
        runAddMissingMethodsTest("generatebuilder/basicautoparcelgson/Test.java",
                "generatebuilder/basicautoparcelgson/Test_expected.java",
                AUTOPARCEL_GSON);

    }

    private void runAddMissingMethodsTest(String inputFile, String expectedFile, String library) {
        myFixture.configureByFiles(inputFile, library);
        myFixture.testAction(new AddMissingMethodsToBuilderAction());
        myFixture.checkResultByFile(expectedFile, true);
    }


}
