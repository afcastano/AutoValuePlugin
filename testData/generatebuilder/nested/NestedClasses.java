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
            <caret>
            public abstract Date aja();
            public abstract int getIntValue();
            public abstract Boolean isSomething();
        }

    }

}