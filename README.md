# Trackcat Android App

This application represents the client application of the [Trackcat server](https://github.com/timokramer4/trackcat-server) on Android.

## Preparation

### Development envoirement

Android Studio is required as the development environment to compile and run the application. Changing the build gradle from 3.4.2 to a new version causes unexpected compiler issues related to the used dependencies.

### Configure server API

In order for the client to communicate with the API of the correlating server, the address of the server must be set correctly in the `APIConnector.java` class. The section to be adjusted for this looks as follows:

```java
retrofit = new Retrofit.Builder()
    .baseUrl("http://YOUR_SERVER_ADDRESS:5000/")
    .addConverterFactory(GsonConverterFactory.create())
    .build();
```

After the server's API interface is set correctly, the application can be compiled and used.

NOTE: The application can be launched without the server running, but logging in, registering, or interacting beyond the login window is not possible without working server connection.

## Supported versions

Die folgenden Android SDK Versionen werden von dieser App unterstützt:

 -  Minimal SDK: 15
 -  Target SDK: 29