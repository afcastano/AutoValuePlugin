package com.afcastano.intellij.autovalue;

import com.afcastano.intellij.autovalue.actions.GenerateAutoValueBuilderAction;
import com.afcastano.intellij.autovalue.actions.GenerateAutoValueCreateAction;
import com.afcastano.intellij.autovalue.actions.UpdateGeneratedMethodsAction;
import com.afcastano.intellij.autovalue.intentions.AddBuilderIntention;
import com.afcastano.intellij.autovalue.intentions.AddCreateMethodIntention;
import com.afcastano.intellij.autovalue.intentions.UpdateGeneratedMethodsIntention;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.testFramework.fixtures.JavaCodeInsightTestFixture;

public class AutoValuePluginTestUtils {

    public static final String AUTOVALUE = "com/google/auto/value/AutoValue.java";
    public static final String AUTOPARCEL = "auto/parcel/AutoParcel.java";
    public static final String AUTOPARCEL_GSON = "auto/parcelgson/AutoParcelGson.java";


    private JavaCodeInsightTestFixture myFixture;

    public AutoValuePluginTestUtils(JavaCodeInsightTestFixture myFixture) {
        this.myFixture = myFixture;
    }

    /**
     * Tests the AddBuilderIntention and GenerateAutoValueAction with the files provided.
     */
    public void runGenerateBuilderActions(String expectedFile, String... filesToLoad) {
        configureSourceFiles(filesToLoad);
        runIntention(new AddBuilderIntention(), expectedFile);
        runAction(new GenerateAutoValueBuilderAction(), expectedFile);
    }

    /**
     * Tests the AddCreateMethodIntention and GenerateAutoValueCreateActionAction with the files provided.
     */
    public void runGenerateCreateMethodActions(String expectedFile, String... filesToLoad) {
        configureSourceFiles(filesToLoad);
        runIntention(new AddCreateMethodIntention(), expectedFile);
        runAction(new GenerateAutoValueCreateAction(), expectedFile);
    }

    /**
     * Tests the UpdateBuilderIntention and UpdateAutoValueBuilderAction with the files provided.
     */
    public void runUpdateMethodsActions(String expectedFile, String... filesToLoad) {
        configureSourceFiles(filesToLoad);
        runIntention(new UpdateGeneratedMethodsIntention(), expectedFile);
        runAction(new UpdateGeneratedMethodsAction(), expectedFile);
    }

    public void configureSourceFiles(String... files) {
        myFixture.configureByFiles(files);
    }

    public void runIntention(IntentionAction action, String expectedFile) {
        myFixture.launchAction(action);
        myFixture.checkResultByFile(expectedFile, true);
    }

    public void runAction(AnAction action, String expectedFile) {
        myFixture.testAction(action);
        myFixture.checkResultByFile(expectedFile, true);
    }
}
