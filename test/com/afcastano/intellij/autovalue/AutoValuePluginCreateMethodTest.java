package com.afcastano.intellij.autovalue;

import com.afcastano.intellij.autovalue.actions.GenerateAutoValueBuilderAction;
import com.afcastano.intellij.autovalue.actions.GenerateAutoValueCreateAction;
import com.afcastano.intellij.autovalue.actions.UpdateGeneratedMethodsAction;
import com.afcastano.intellij.autovalue.intentions.AddBuilderIntention;
import com.afcastano.intellij.autovalue.intentions.AddCreateMethodIntention;
import com.afcastano.intellij.autovalue.intentions.UpdateGeneratedMethodsIntention;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.testFramework.fixtures.LightCodeInsightFixtureTestCase;
import org.jetbrains.annotations.NonNls;

import static com.afcastano.intellij.autovalue.AutoValuePluginTestUtils.AUTOVALUE;

public class AutoValuePluginCreateMethodTest extends LightCodeInsightFixtureTestCase {

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

    //---- generate create method
    public void testGenerateBasicCreateMethodWorks() {
        utils.runGenerateCreateMethodActions("generatecreatemethod/basic/BasicTestFile_expected.java",
                "generatecreatemethod/basic/BasicTestFile.java",
                AUTOVALUE);
    }


    public void testGenerateBasicCreateMethodWorksWhenBuilderExists() {
        utils.runGenerateCreateMethodActions("generatecreatemethod/builderexist/Test_expected.java",
                "generatecreatemethod/builderexist/Test.java",
                AUTOVALUE);
    }

    public void testUpdateWhenAddNewPropertyAtTheEnd() {
        utils.runUpdateMethodsActions("generatecreatemethod/addNewPropertyAtTheEnd/Test_expected.java",
                "generatecreatemethod/addNewPropertyAtTheEnd/Test.java",
                AUTOVALUE);
    }

    public void testUpdateWhenAddNewPropertyInTheMiddle() {
        utils.runUpdateMethodsActions("generatecreatemethod/addNewPropertyInTheMiddle/Test_expected.java",
                "generatecreatemethod/addNewPropertyInTheMiddle/Test.java",
                AUTOVALUE);
    }

    public void testRemovePropertyAtTheEnd() {
        utils.runUpdateMethodsActions("generatecreatemethod/removeLastProperty/Test_expected.java",
                "generatecreatemethod/removeLastProperty/Test.java",
                AUTOVALUE);
    }

    public void testRemovePropertyInTheMiddle() {
        utils.runUpdateMethodsActions("generatecreatemethod/removePropertyInTheMiddle/Test_expected.java",
                "generatecreatemethod/removePropertyInTheMiddle/Test.java",
                AUTOVALUE);
    }

    public void testUpdateBuilderWhenCreateOk() {
        utils.runUpdateMethodsActions("generatecreatemethod/updateBuilderWhenCreateOk/Test_expected.java",
                "generatecreatemethod/updateBuilderWhenCreateOk/Test.java",
                AUTOVALUE);
    }

    public void testUpdateCreateWhenBuilderOk() {
        utils.runUpdateMethodsActions("generatecreatemethod/updateCreateWhenBuilderOk/Test_expected.java",
                "generatecreatemethod/updateCreateWhenBuilderOk/Test.java",
                AUTOVALUE);
    }

    public void testGenerateOkWhenNoMethod() {
        utils.runGenerateCreateMethodActions("generatecreatemethod/nomethods/Test_expected.java",
                "generatecreatemethod/nomethods/Test.java",
                AUTOVALUE);
    }

    public void testUpdateOkWhenNoMethod() {
        utils.runUpdateMethodsActions("generatecreatemethod/nomethods/Test.java",
                "generatecreatemethod/nomethods/Test.java",
                AUTOVALUE);
    }

}
