# AutoValuePlugin
[Google AutoValue](https://github.com/google/auto) plugin for IntelliJ

It also supports [AutoParcel](https://github.com/frankiesardo/auto-parcel) and [AutoParceGson](https://github.com/evant/auto-parcel-gson).
Thanks to [@vjames19](https://github.com/vjames19) for that!

AutoValue is awesome.
I can't explain it better than [that](https://github.com/google/auto/tree/master/value).

Functionality:

- Adds an @Autovalue.Builder static class inside the target @Autovalue class.
- Searches for all abstract getters on the target class and transforms it into builder methods in the builder.
- Creates a static builder method that returns the @Autovalue.Builder instance.
- If the builder exists, it will add the missing properties to it.

Here is a short example video (1:50 mins):
https://www.youtube.com/watch?v=8_HbI9RwiGw

