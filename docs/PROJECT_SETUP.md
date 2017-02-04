# Project Set up
This tutorial was created using IntelliJ IDEA 2016.3. Should be similar for other versions including Android Studio.

### Pre-requisites
According to the [official guidelines](https://www.jetbrains.com/help/idea/2016.3/plugin-development-guidelines.html#d1348165e8):

1. Download and install a JDK.
2. Make sure that the Plugin DevKit plugin is enabled.
3. Optionally, [download](http://www.jetbrains.org/display/IJOS/Download) the IntelliJ IDEA Community Edition sources. This will make debugging your plugins much easier.
4. You also need to configure IntelliJ Platform Plugin SDK. Instructions [here](https://www.jetbrains.com/help/idea/2016.3/configuring-intellij-platform-plugin-sdk.html)

### IDE Set up

1. Open up IntelliJ and select *Create project from version control*
![alt tag](https://raw.githubusercontent.com/afcastano/AutoValuePlugin/master/docs/img/GitHub.png)
2. Select GitHub and enter `https://github.com/afcastano/AutoValuePlugin.git`. Click *clone*
3. Select *Create project from existing sources* and click next
4. Select location and name and click next
5. Un-select all folders except the first two corresponding to `src` and `test`
![alt tag][module-folders]
6. If a library is detected, remove it. In this case `AutoValuePlugin` on the dist folder might have been detected. Un-select it and click next.
![aja][Library]
7. In the module structure dialog, select the module and click next.
![aja][DefaultModule]
8. A warning will appear indicating that the module already exists. **Click Reuse**.
![aja][UseExisting]
9. After this, make sure the *IntelliJ Platform Plugin SDK* appears on the list (According to [pre-requisites](#pre-requisites) ) and hit next.
![aja][ConfigureSDK]
10. Click Finish.
11. Go to Project settings and select `7 - Diamonds` as the Language level. Click OK.
![aja][Java7]

That's it, the project should be configured and ready to go.

### Testing the plugin
- To run the tests, Right click on the test folder and **Run 'All Tests'**

- To manually test the plugin:
  * Create a new run configuration of type plugin, Give any name and select AutoValuePlugin for the "Use Classpath of module" box.
  * Run the new configuration, it will fire up a new instance of IntelliJ with the development version of the plugin installed.
![aja][Run]


[module-folders]:(https://raw.githubusercontent.com/afcastano/AutoValuePlugin/tree/master/docs/img/ModuleFolders.png)
[Library]:(https://github.com/afcastano/AutoValuePlugin/tree/master/docs/img/Library.png)
[DefaultModule]:(https://github.com/afcastano/AutoValuePlugin/tree/master/docs/img/DefaultModule.png)
[UseExisting]:(https://github.com/afcastano/AutoValuePlugin/tree/master/docs/img/UseExisting.png)
[ConfigureSDK]:(https://github.com/afcastano/AutoValuePlugin/tree/master/docs/img/ConfigureSDK.png)
[Java7]:(https://github.com/afcastano/AutoValuePlugin/tree/master/docs/img/Java7.png)
[Run]:(https://github.com/afcastano/AutoValuePlugin/tree/master/docs/img/Run.png)
