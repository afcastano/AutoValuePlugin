Release 20170219
----------------
* 2017-02-19: Ignores java beans style if not all properties comply.

Release 20170208
----------------
* 2017-02-08: Ignores `android.os.Parcelable` and `java.util.*` from interface method generation. [#14](https://github.com/afcastano/AutoValuePlugin/issues/14)

Release 20170203
-------
* 2017-02-03: Ignores `toBuilder` based on return type. [#15](https://github.com/afcastano/AutoValuePlugin/issues/15)

Release 20170130
-------
* 2017-01-30: Added support for interfaces.[#14](https://github.com/afcastano/AutoValuePlugin/issues/14)
* 2017-01-30: Ignore `toBuilder` method when generating. [#15](https://github.com/afcastano/AutoValuePlugin/issues/15)

Release 20160728
-------
* 2016-07-28: Improved the way create method is generated according to [#11](https://github.com/afcastano/AutoValuePlugin/issues/11)

Release 20160726
-------
* 2016-07-26: Fixed bug [#13](https://github.com/afcastano/AutoValuePlugin/issues/13)

Release 20160723
-------
* 2016-07-23: Added functionality to optionally generate the create method using the builder. If you generate the new option, both the builder and the create method will be generated.

Release 20160628
-------
* 2016-06-28: Added code intentions and generate menu actions. Actions are now enabled depending if you need to add a builder or update an existing one.

Release 20160222
--------
* 2016-02-22: Added support for javabeans style getters and setters.

Release 20160214
--------
* 2016-02-14: Changed the order of the Builder modifiers to make it JLS compliant.

Release 20160202
--------
* 2016-02-02: Added @AutoParcel and @AutoParcelGson support.

First Release
--------
* 2015-08-12: Fixed error when the action was triggered in a non java file.
