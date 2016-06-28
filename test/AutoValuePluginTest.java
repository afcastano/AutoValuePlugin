import com.afcastano.intellij.autovalue.actions.GenerateAutoValueBuilderAction;
import com.afcastano.intellij.autovalue.actions.UpdateAutoValueBuilderAction;
import com.afcastano.intellij.autovalue.intentions.AddBuilderIntention;
import com.afcastano.intellij.autovalue.intentions.UpdateBuilderIntention;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.actionSystem.AnAction;
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
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    public void testSimpleClass() {
        runGenerateBuilderActions("generatebuilder/basic/BasicTestFile_expected.java",
                "generatebuilder/basic/BasicTestFile.java",
                AUTOVALUE);
    }

    public void testNestedClasses() {
        runGenerateBuilderActions("generatebuilder/nested/NestedClasses_expected.java",
                "generatebuilder/nested/NestedClasses.java",
                AUTOVALUE);
    }

    public void testNonJavaFileGenerate() {
        runGenerateBuilderActions("generatebuilder/nonJava/test.js",
                "generatebuilder/nonJava/test.js",
                AUTOVALUE);
    }

    public void testNonJavaFileUpdate() {
        runUpdateBuilderActions("generatebuilder/nonJava/test.js",
                "generatebuilder/nonJava/test.js",
                AUTOVALUE);
    }

    public void testNotAnnotatedGenerate() {
        runGenerateBuilderActions("generatebuilder/notannotated/NotAnnotated.java",
                "generatebuilder/notannotated/NotAnnotated.java",
                AUTOVALUE);
    }

    public void testNotAnnotatedUpdate() {
        runUpdateBuilderActions("generatebuilder/notannotated/NotAnnotated.java",
                "generatebuilder/notannotated/NotAnnotated.java",
                AUTOVALUE);
    }

    //TODO This is not implemented yet.
//    public void testNonAbstractGenerate() {
//        runGenerateBuilderActions("generatebuilder/notabstract/Test.java",
//                "generatebuilder/notabstract/Test.java",
//                AUTOVALUE);
//    }
//
//    public void testNonAbstractUpdate() {
//        runUpdateBuilderActions("generatebuilder/notabstract/Test.java",
//                "generatebuilder/notabstract/Test.java",
//                AUTOVALUE);
//    }

    public void testAddNewProperty() {
        runUpdateBuilderActions("generatebuilder/addnewproperty/Test_expected.java",
                "generatebuilder/addnewproperty/Test.java",
                AUTOVALUE);

    }

    public void testAddBuilderFactory() {
        runUpdateBuilderActions("generatebuilder/addbuilderfactory/Test_expected.java",
                "generatebuilder/addbuilderfactory/Test.java",
                AUTOVALUE);

    }

    public void testRemoveProperty() {
        runUpdateBuilderActions("generatebuilder/removeproperty/Test_expected.java",
                "generatebuilder/removeproperty/Test.java",
                AUTOVALUE);
    }

    public void testJavaBeanStyle() {
        runGenerateBuilderActions("generatebuilder/javabeanstyle/Test_expected.java",
                "generatebuilder/javabeanstyle/Test.java",
                AUTOVALUE);

    }


    public void testBasicAutoParcel() {
        runGenerateBuilderActions("generatebuilder/basicautoparcel/Test_expected.java",
                "generatebuilder/basicautoparcel/Test.java",
                AUTOPARCEL);

    }

    public void testBasicAutoParcelGson() {
        runGenerateBuilderActions("generatebuilder/basicautoparcelgson/Test_expected.java",
                "generatebuilder/basicautoparcelgson/Test.java",
                AUTOPARCEL_GSON);

    }

    public void testGeneratedSourcesAlreadyExist() {
        runGenerateBuilderActions("generatebuilder/alreadyhasgeneratedsources/Test_expected.java",
                "generatebuilder/alreadyhasgeneratedsources/Test.java",
                "generatebuilder/alreadyhasgeneratedsources/AutoValue_BasicTestFile.java",
                AUTOVALUE);
    }


    /**
     * Tests the AddBuilderIntention and GenerateAutoValueAction with the files provided.
     */
    private void runGenerateBuilderActions(String expectedFile, String... filesToLoad) {
        configureSourceFiles(filesToLoad);
        runIntention(new AddBuilderIntention(), expectedFile);
        runAction(new GenerateAutoValueBuilderAction(), expectedFile);
    }

    /**
     * Tests the UpdateBuilderIntention and UpdateAutoValueBuilderAction with the files provided.
     */
    private void runUpdateBuilderActions(String expectedFile, String... filesToLoad) {
        configureSourceFiles(filesToLoad);
        runIntention(new UpdateBuilderIntention(), expectedFile);
        runAction(new UpdateAutoValueBuilderAction(), expectedFile);
    }

    private void configureSourceFiles(String... files) {
        myFixture.configureByFiles(files);
    }

    private void runIntention(IntentionAction action, String expectedFile) {
        myFixture.launchAction(action);
        myFixture.checkResultByFile(expectedFile, true);
    }

    private void runAction(AnAction action, String expectedFile) {
        myFixture.testAction(action);
        myFixture.checkResultByFile(expectedFile, true);
    }

}
