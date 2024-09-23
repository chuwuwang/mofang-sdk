#GENERAL
YSDK works with all morefun smart POS. All APIs are built based on standard Android mechanism, and you need to add a JAR file to your project directory. The morefun POS application development environment is the same as Android application development environment.

# YSDK
YSDK is a service installed in the smart POS. After the APP is bind to the ysdk service, it can invoke the methods in the service through the AIDL interface.

# YDemo
YDemo is an example written for the ysdk programming manual.

# DOCUMENT

PDF: (YSDK_Android_Programming_Manual.pdf)


# START GUIDE
This guide is a quick start guide to developing smart pos applications based on ysdk for Android applications.

##Step 1: Download and install Android Studio
Follow the Android Studio compiler installation guide to download and install Android Studio. (Note: download address is Google official website)


##Step 2: Create the project
Follow these steps to create a new Empty Activity application project.

1. Start Android Studio. If you see the Welcome to Android Studio dialog box, select Start a new Android Studio project, otherwise, click File in the Android Studio menu bar, Then click New->New Project and enter your application name, company domain, and project location as prompted. Then click Next.
2. Select the model you need for your application. If you're not sure what you need, just select Phone and Tablet. Then click Next.
3. In the Add an activity to Mobile dialog box, select Empty Activity. Then click Next.
4. Enter the Activity name, layout name, and title as prompted. Use the default values. Then click Finish.


##Step 3: Download and install the development kit

Download the SDK from http://git.morefun-et.com:8098/morefun/MF-YDemo and unzip it.

After decompression, you will get a jar file, please add the jar file to the project. There is also a ysdk.apk file that needs to be installed into the POS device.

##Step 4: Hello morefun smart pos application




###Integrated SDK

1. Install YSDK-XXX_productNormal_release.apk

2. Copy the jar file to the libs folder
Copy the downloaded location SDK jar file to the libs directory of the project. If any location jar file of an earlier version exists, delete it. No so library file support required.

3. Configure the build.gradle file
Configure implementation fileTree(dir: 'libs', include: ['*.aar', '*.jar']) in dependencies of the build.gradle file.

4. Binding YSDK service in Application onCreate function(Note that the strings in setAction and setPackage cannot be changed)


        Intent intent = new Intent();
        intent.setAction("com.morefun.ysdk.service");
        intent.setPackage("com.morefun.ysdk");
        bindService(intent, connection, Context.BIND_AUTO_CREATE);


5. Register a ServiceConnection

    private ServiceConnection connection = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName name) {
            deviceServiceEngine = null;
            Log.e(TAG, "======onServiceDisconnected======");
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            deviceServiceEngine = DeviceServiceEngine.Stub.asInterface(service);
            Log.d(TAG, "======onServiceConnected======");

            linkToDeath(service);
        }

        private void linkToDeath(IBinder service) {
            try {
                service.linkToDeath(new IBinder.DeathRecipient() {
                    @Override
                    public void binderDied() {
                        Log.d(TAG, "======binderDied======");
                        deviceServiceEngine = null;
                        bindDeviceService();
                    }
                }, 0);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
    };

6. After the service connection is successful, it can use the aidl interface inside

##Step 5: Connect the Morefun POS device
The easiest way to see how well your app is actually running is to connect your Android device to your computer. Follow the instructions to enable developer options on your Android device and configure your app and system to detect the device.

##Step 6: Build and run your application
In Android Studio, tap the Run menu option (or the play button icon) to run your app.

When prompted to select a device, select one of the following options:

In addition, Android Studio will call Gradle to build your app and then display the results on the device. It may take a few minutes for the app to open.

#Subsequent steps
You might want to look at some sample code, see YDemo.

Or you can go to the development Guide and read more about it.
