import com.google.auto.value.AutoValue;

import java.util.Date;

@AutoValue
public abstract class TestAuto {

    public abstract String value();
    public abstract TestAuto2 testAuto();
    public abstract int bla();
    public abstract int yeah();

    @AutoValue
    public static abstract class InnerAuto {
        public abstract Integer newVal();
        public abstract TestAuto2 aja();

        @AutoValue
        public static abstract class LastInner {

            public abstract Date aja();
            public abstract int getIntValue();
            public abstract Boolean isSomething();

            public static Builder builder() {
                return new AutoValue_TestAuto_InnerAuto_LastInner.Builder();
            }

            @AutoValue.Builder
            public abstract static class Builder {
                public abstract Builder aja(Date aja);

                public abstract Builder getIntValue(int getIntValue);

                public abstract Builder isSomething(Boolean isSomething);

                public abstract LastInner build();
            }
        }

    }

}