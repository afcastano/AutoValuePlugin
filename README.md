# [AutoValuePlugin](https://plugins.jetbrains.com/plugin/8091?pr=idea)
[Google AutoValue](https://github.com/google/auto) plugin for IntelliJ.

___
### Prerequisites
IntelliJ should run using **JRE 1.7** or higher.
The JRE version is shown in the about dialog.

If you need to upgrade the JRE, follow this [guide](https://intellij-support.jetbrains.com/hc/en-us/articles/206544879-Selecting-the-JDK-version-the-IDE-will-run-under).
___

AutoValue is awesome.
I can't explain it better than [that](https://github.com/google/auto/blob/master/value/userguide/index.md), or [that](https://docs.google.com/presentation/d/14u_h-lMn7f1rXE1nDiLX0azS3IkgjGl5uxp5jGJ75RE/edit#slide=id.g2a5e9c4a8_00).

### Functionality:

- Adds an @AutoValue.Builder static class inside the target @AutoValue class.
- Searches for all abstract getters on the target class and transforms it into builder methods in the builder.
- Creates a static builder method that returns the @AutoValue.Builder instance.
- Optionally, generates the [create](https://github.com/google/auto/blob/master/value/userguide/index.md#in-your-value-class) factory method. [See #11](https://github.com/afcastano/AutoValuePlugin/issues/11)
- If the builder exists, it will either add the missing properties to it or remove the ones that are not needed any more.
- It also supports [AutoParcel](https://github.com/frankiesardo/auto-parcel) and [AutoParceGson](https://github.com/evant/auto-parcel-gson). Thanks to [@vjames19](https://github.com/vjames19)!

### How to use:

The plugin adds new context menu actions, [code generation actions](https://www.jetbrains.com/help/idea/2016.1/generating-code.html) and [intention actions](https://www.jetbrains.com/help/idea/2016.1/intention-actions.html). The easiest way to use the plugin is to right-click inside a class annotated with [@AutoValue](https://github.com/google/auto) and choose any of the generation options that appears first on the list.

There are also other ways of using the plugin. Here is a short tutorial video (1:25 mins):

https://www.youtube.com/watch?v=sMX9PT3ecu8
