package com.afcastano.intellij.autovalue;

import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;

import static com.afcastano.intellij.autovalue.AutoValuePluginTestUtils.AUTOPARCEL;
import static com.afcastano.intellij.autovalue.AutoValuePluginTestUtils.AUTOPARCEL_GSON;
import static com.afcastano.intellij.autovalue.AutoValuePluginTestUtils.AUTOVALUE;

public class AutoValuePluginBuilderTest extends LightCodeInsightFixtureTestCase {

    private AutoValuePluginTestUtils utils;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        utils = new AutoValuePluginTestUtils(myFixture);
    }

    @Override
    protected String getTestDataPath() {
        return "testData";
    }

    public void testSimpleClass() {
        utils.runGenerateBuilderActions("generatebuilder/basic/BasicTestFile_expected.java",
                "generatebuilder/basic/BasicTestFile.java",
                AUTOVALUE);
    }

    public void testNestedClasses() {
        utils.runGenerateBuilderActions("generatebuilder/nested/NestedClasses_expected.java",
                "generatebuilder/nested/NestedClasses.java",
                AUTOVALUE);
    }

    public void testNonJavaFileGenerate() {
        utils.runGenerateBuilderActions("generatebuilder/nonJava/test.js",
                "generatebuilder/nonJava/test.js",
                AUTOVALUE);
    }

    public void testNonJavaFileUpdate() {
        utils.runUpdateMethodsActions("generatebuilder/nonJava/test.js",
                "generatebuilder/nonJava/test.js",
                AUTOVALUE);
    }

    public void testNotAnnotatedGenerate() {
        utils.runGenerateBuilderActions("generatebuilder/notannotated/NotAnnotated.java",
                "generatebuilder/notannotated/NotAnnotated.java",
                AUTOVALUE);
    }

    public void testNotAnnotatedUpdate() {
        utils.runUpdateMethodsActions("generatebuilder/notannotated/NotAnnotated.java",
                "generatebuilder/notannotated/NotAnnotated.java",
                AUTOVALUE);
    }

    public void testNoMethodsGenerate() {
        utils.runGenerateBuilderActions("generatebuilder/nomethods/Test_expected.java",
                "generatebuilder/nomethods/Test.java",
                AUTOVALUE);
    }

    public void testNoMethodsUpdate() {
        utils.runUpdateMethodsActions("generatebuilder/nomethods/Test.java",
                "generatebuilder/nomethods/Test.java",
                AUTOVALUE);
    }

    //TODO This is not implemented yet.
//    public void testNonAbstractGenerate() {
//        utils.runGenerateBuilderActions("generatebuilder/notabstract/Test.java",
//                "generatebuilder/notabstract/Test.java",
//                AUTOVALUE);
//    }
//
//    public void testNonAbstractUpdate() {
//        utils.runUpdateMethodsActions("generatebuilder/notabstract/Test.java",
//                "generatebuilder/notabstract/Test.java",
//                AUTOVALUE);
//    }

    public void testAddNewProperty() {
        utils.runUpdateMethodsActions("generatebuilder/addnewproperty/Test_expected.java",
                "generatebuilder/addnewproperty/Test.java",
                AUTOVALUE);

    }

    public void testIngoreToBuilderMethodWhenAdding() {
        utils.runUpdateMethodsActions("generatebuilder/ignoretobuilderwhenadding/Test_expected.java",
                "generatebuilder/ignoretobuilderwhenadding/Test.java",
                AUTOVALUE);

    }

    public void testIngoreToBuilderMethodFirstTime() {
        utils.runGenerateBuilderActions("generatebuilder/ignoretobuilderfirsttime/Test_expected.java",
                "generatebuilder/ignoretobuilderfirsttime/Test.java",
                AUTOVALUE);

    }

    public void testAddBuilderFactory() {
        utils.runUpdateMethodsActions("generatebuilder/addbuilderfactory/Test_expected.java",
                "generatebuilder/addbuilderfactory/Test.java",
                AUTOVALUE);

    }

    public void testRemoveProperty() {
        utils.runUpdateMethodsActions("generatebuilder/removeproperty/Test_expected.java",
                "generatebuilder/removeproperty/Test.java",
                AUTOVALUE);
    }

    public void testJavaBeanStyle() {
        utils.runGenerateBuilderActions("generatebuilder/javabeanstyle/Test_expected.java",
                "generatebuilder/javabeanstyle/Test.java",
                AUTOVALUE);

    }


    public void testBasicAutoParcel() {
        utils.runGenerateBuilderActions("generatebuilder/basicautoparcel/Test_expected.java",
                "generatebuilder/basicautoparcel/Test.java",
                AUTOPARCEL);

    }

    public void testBasicAutoParcelGson() {
        utils.runGenerateBuilderActions("generatebuilder/basicautoparcelgson/Test_expected.java",
                "generatebuilder/basicautoparcelgson/Test.java",
                AUTOPARCEL_GSON);

    }

    public void testGeneratedSourcesAlreadyExist() {
        utils.runGenerateBuilderActions("generatebuilder/alreadyhasgeneratedsources/Test_expected.java",
                "generatebuilder/alreadyhasgeneratedsources/Test.java",
                "generatebuilder/alreadyhasgeneratedsources/AutoValue_BasicTestFile.java",
                AUTOVALUE);
    }

    public void testGenerateBuilderWhenCreateExists() {
        utils.runGenerateBuilderActions("generatebuilder/generateBuilderWhenCreateExists/Test_expected.java",
                "generatebuilder/generateBuilderWhenCreateExists/Test.java",
                AUTOVALUE);
    }

    public void testGenerateBuilderWithInterfaceCorrectly() {
        utils.runGenerateBuilderActions("generatebuilder/withinterface/BasicTestFile_expected.java",
                "generatebuilder/withinterface/BasicTestFile.java",
                "test/Interface1.java",
                AUTOVALUE);
    }

    public void testGenerateBuilderWithInterfaceIgnoringBlacklistedCorrectly() {
        utils.runGenerateBuilderActions("generatebuilder/withinterfaceignoresblacklisted/BasicTestFile_expected.java",
                "generatebuilder/withinterfaceignoresblacklisted/BasicTestFile.java",
                "test/Interface1.java",
                "java/util/Map.java",
                "android/os/Parcelable.java",
                AUTOVALUE);
    }

    public void testGenerateBuilderWithInterfaceHierarchy() {
        utils.runGenerateBuilderActions("generatebuilder/withinterfacehierarchy/BasicTestFile_expected.java",
                "generatebuilder/withinterfacehierarchy/BasicTestFile.java",
                "test/Interface1.java",
                "test/i2/Interface2.java",
                "test/i3/Interface3.java",
                AUTOVALUE);
    }

}
