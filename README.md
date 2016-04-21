# AutoValuePlugin
[Google AutoValue](https://github.com/google/auto) plugin for IntelliJ

AutoValue is awesome.
I can't explain it better than [that](https://github.com/google/auto/blob/master/value/userguide/index.md), or [that](https://docs.google.com/presentation/d/14u_h-lMn7f1rXE1nDiLX0azS3IkgjGl5uxp5jGJ75RE/edit#slide=id.g2a5e9c4a8_00).

Functionality:

- Adds an @AutoValue.Builder static class inside the target @AutoValue class.
- Searches for all abstract getters on the target class and transforms it into builder methods in the builder.
- Creates a static builder method that returns the @AutoValue.Builder instance.
- If the builder exists, it will add the missing properties to it.
- It also supports [AutoParcel](https://github.com/frankiesardo/auto-parcel) and [AutoParceGson](https://github.com/evant/auto-parcel-gson). Thanks to [@vjames19](https://github.com/vjames19)!


Here is a short tutorial video (1:50 mins):
https://www.youtube.com/watch?v=8_HbI9RwiGw
