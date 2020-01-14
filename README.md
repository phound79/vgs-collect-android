[![CircleCI](https://circleci.com/gh/verygoodsecurity/vgs-collect-android/tree/master.svg?style=svg&circle-token=24087545f8aff3cee11ebe55330d2df778a7bb1f)](https://circleci.com/gh/verygoodsecurity/vgs-collect-android/tree/master)
[![UT](https://img.shields.io/badge/Unit_Test-pass-green)]()
[![license](https://img.shields.io/badge/License-MIT-green.svg)](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE)

# VGS Collect SDK 

VGS Collect - is a product suite that allows customers to collect information securely without possession of it. VGS Collect Android SDK allows you to securely collect data from your users via forms without having to have that data pass through your systems. The form fields behave like traditional input fields while securing access to the unsecured data.

Table of contents
=================

<!--ts-->
   * [Dependencies](#dependencies)
   * [Structure](#structure)
   * [Integration](#integration)
      * [Add the SDK to your project](#add-the-sdk-to-your-project)
      * [Add permissions](#add-permissions)
   * [Usage](#usage)
      * [Session initialization](#session-initialization)
      * [Submit information](#submit-information)
      * [Fields state tracking](#fields-state-tracking)
      * [Handle service response](#handle-service-response)
      * [End session](#end-session)
   * [License](#license)
<!--te-->

<p align="center">
<img src="https://github.com/verygoodsecurity/vgs-collect-android/blob/master/vgs-collect-android-state.png" width="200" alt="VGS Collect Android SDK States" hspace="20"><img src="https://github.com/verygoodsecurity/vgs-collect-android/blob/master/vgs-collect-android-response.png" width="200" alt="VGS Collect Android SDK Response" hspace="20">
</p>

## Dependencies

| Dependency | Version |
| :--- | :---: |
| Min SDK | 14 |
| Target SDK | 28 |
| com.android.support:appcompat-v7 | 28.0.0 |
| androidx.appcompat:appcompat | 1.1.0 |
| androidx.core:core-ktx | 1.1.0 |
| com.android.support:design | 28.0.0 |
| org.jetbrains.kotlin:kotlin-stdlib-jdk7 | 1.3.50 |

## Structure
* **VGSCollect SDK** - provides an API for interacting with the VGS Vault
* **app** - sample application to act as the host app for testing the SDK during development

## Integration 
For integration you need to install the [Android Studio](http://developer.android.com/sdk/index.html) and a [JDK](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) on your machine.

#### Add the SDK to your project
If you are using Maven, add the following to your `build.gradle` file:
```
dependencies {
   implementation 'com.verygoodsecurity:vgscollect:1.0.0’
}
```
#### Add permissions
The **SDK** requires the following permissions. Please add them to your `AndroidManifest.xml` file if they are not already present:
```
<uses-permission android:name="android.permission.INTERNET"/>
```

## Usage

#### Session initialization
Add VGSEditText to your layout file:
```
<?xml version="1.0" encoding="utf-8"?>

<com.verygoodsecurity.vgscollect.widget.VGSEditText
  	 android:id="@+id/my_secure_view"
   	 android:layout_width="match_parent"
  	 android:layout_height="match_parent" />
```

Add the following code to initialize the SDK to your Activity or Fragment class:
```
public class ExampleActivity extends Activity {
   private VGSCollect vgsForm = new VGSCollect( user_key, Environment.SANDBOX);
   
   @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.examle_layout);
        
        vgsForm.addOnFieldStateChangeListener(...)
        vgsForm.setOnResponseListener(...)
        
        VGSEditText yourField = findViewById(R.id.your_field);
        vgsForm.bindView(yourField);
    }
    
    private void submitData() {
        //...
    }
    
    @Override
    protected void onDestroy() {
        //...
    }
}
```

#### Submit information
Call `asyncSubmit` or `submit` to execute and send data on VGS Server if you want to handle multithreading by yourself:
```
private void submitData() {
    //..
    vgsForm.asyncSubmit(this, "/path", HTTPMethod.POST);
}
```

There is an option to send custom fields in the same request that the SDK CTA sends:
```
private void submitData() {
    //..
    val data = HashMap<String, String>();
    data["key"] = "value";
    vgsForm.setCustomData(data);
    
    vgsForm.asyncSubmit(this@MainActivity, "/path", HTTPMethod.POST);
}
```

More to the point SDK allows send your custom headers:
```
private void submitData() {
    //..
    val headers = HashMap<String, String>();
    headers["key"] = "value";
    vgsForm.setCustomHeaders(headers);
    
    vgsForm.asyncSubmit(this, "/path", HTTPMethod.POST);
}
```

To clear all custom headers use `resetCustomHeaders` or `resetCustomData` to clear custom fields added before.
```
private void submitData() {
    vgsForm.resetCustomHeaders();
    
    val headers = HashMap<String, String>();
    headers["key"] = "value";
    vgsForm.setCustomHeaders(headers);
    
    vgsForm.asyncSubmit(this, "/path", HTTPMethod.POST);
}
```

#### Fields state tracking
Whenever an EditText changes, **VGSCollect** can notify user about it. Implement `OnFieldStateChangeListener` to observe changes:
```
  vgsForm.addOnFieldStateChangeListener(new OnFieldStateChangeListener() {
            @Override
            public void onStateChange(FieldState state) {
                //...
            }
        });
```

#### Handle service response
You need to implement `VgsCollectResponseListener` to read response:
```
  vgsForm.setOnResponseListener(new VgsCollectResponseListener() {
            @Override
            public void onResponse(@org.jetbrains.annotations.Nullable VGSResponse response) {
                //...
            }
        });
```

#### End session
Finish work with **VGSCollect** by calling `onDestroy` inside android onDestroy callback:
```
    @Override
    protected void onDestroy() {
        super.onDestroy();
        vgsForm.onDestroy();
    }
 ```

## License
VGSCollect Android SDK is released under the MIT license. [See LICENSE](https://github.com/verygoodsecurity/vgs-collect-android/blob/master/LICENSE) for details.
