import com.afcastano.intellij.autovalue.actions.AddBuilderAction;
import com.afcastano.intellij.autovalue.actions.UpdateBuilderAction;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NonNls;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AutoValuePluginTest extends LightCodeInsightFixtureTestCase {

    @NonNls
    private static final String AUTOVALUE = "com/google/auto/value/AutoValue.java";
    private static final String AUTOPARCEL = "auto/parcel/AutoParcel.java";
    private static final String AUTOPARCEL_GSON = "auto/parcelgson/AutoParcelGson.java";

    @Override
    protected void setUp() throws Exception {
//        VfsRootAccess.SHOULD_PERFORM_ACCESS_CHECK = false; // TODO: a workaround for v15
        super.setUp();
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    public void testSimpleClass() {

        runIntentionAction(new AddBuilderAction(), "generatebuilder/basic/BasicTestFile.java",
                "generatebuilder/basic/BasicTestFile_expected.java",
                AUTOVALUE);

    }

    public void testNestedClasses() {

        runIntentionAction(new AddBuilderAction(), "generatebuilder/nested/NestedClasses.java",
                "generatebuilder/nested/NestedClasses_expected.java",
                AUTOVALUE);

    }

    public void testNonJavaFile() {

        runIntentionAction(new AddBuilderAction(), "generatebuilder/nonJava/test.js",
                "generatebuilder/nonJava/test.js",
                AUTOVALUE);

    }

    public void testNotAnnotated() {

        runIntentionAction(new AddBuilderAction(), "generatebuilder/notannotated/NotAnnotated.java",
                "generatebuilder/notannotated/NotAnnotated.java",
                AUTOVALUE);

    }

    public void testAddNewProperty() {
        runIntentionAction(new UpdateBuilderAction(), "generatebuilder/addnewproperty/Test.java",
                "generatebuilder/addnewproperty/Test_expected.java",
                AUTOVALUE);

    }

    public void testAddBuilderFactory() {
        runIntentionAction(new UpdateBuilderAction(), "generatebuilder/addbuilderfactory/Test.java",
                "generatebuilder/addbuilderfactory/Test_expected.java",
                AUTOVALUE);

    }

    public void testRemoveProperty() {
        runIntentionAction(new UpdateBuilderAction(), "generatebuilder/removeproperty/Test.java",
                "generatebuilder/removeproperty/Test_expected.java",
                AUTOVALUE);
    }

    public void testJavaBeanStyle() {
        runIntentionAction(new AddBuilderAction(), "generatebuilder/javabeanstyle/Test.java",
                "generatebuilder/javabeanstyle/Test_expected.java",
                AUTOVALUE);

    }


    public void testBasicAutoParcel() {
        runIntentionAction(new AddBuilderAction(), "generatebuilder/basicautoparcel/Test.java",
                "generatebuilder/basicautoparcel/Test_expected.java",
                AUTOPARCEL);

    }

    public void testBasicAutoParcelGson() {
        runIntentionAction(new AddBuilderAction(), "generatebuilder/basicautoparcelgson/Test.java",
                "generatebuilder/basicautoparcelgson/Test_expected.java",
                AUTOPARCEL_GSON);

    }

    public void testGeneratedSourcesAlreadyExist() {
        runIntentionAction(new AddBuilderAction(), "generatebuilder/alreadyhasgeneratedsources/Test.java",
                "generatebuilder/alreadyhasgeneratedsources/Test_expected.java",
                "generatebuilder/alreadyhasgeneratedsources/AutoValue_BasicTestFile.java",
                AUTOVALUE);
    }

    private void runIntentionAction(IntentionAction action, String inputFile, String expectedFile, String... extraFiles) {
        List<String> files = new ArrayList<>();
        files.add(inputFile);
        files.addAll(Arrays.asList(extraFiles));

        myFixture.configureByFiles(files.toArray(new String[]{}));
        myFixture.launchAction(action);
        myFixture.checkResultByFile(expectedFile, true);
    }

}
