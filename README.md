# EasyIPC
Android IPC made painless

## Core idea
This library makes IPC (AILD) seems like a type of bus that uses the following 3 simple methods:

```Java
addListener(Class<T>, Listener<T>);
removeListener(Class<T>, Listener<T>);
dispatchEvent(Parcelable);
```

Also provides glue glasses around them to make the implementation as painless and easy as possible.

## How-to
###### The service
Make your service extends from `IpcService.java`.
From this service you can straight call the three methods above.

If you don't want to extends that service, feel free to copy the 15 lines of code from it, into your own service.

###### The Activity
For the activity there's an `IpcActivityHelper.java`.

- Instantiate it during `onCreate`,
- Call `ipcHelper.start()` during Activity `onStart()`
- Call `ipcHelper.stop()` during Activity `onStop()`

The helper will deal with service connection and binding. Call the above mentioned 3 methods directly on the ipcHelper.

###### Bonus points
Use the new (as of July-2017) Android Architecture components

Get and initialise an `IpcViewModel.java`:
```Java
viewModel = ViewModelProviders
		.of(this)
		.get(IpcViewModel.class)
		.init(MainService.class, BIND_AUTO_CREATE); // Service class and Service connection flags
```

Use `LiveData` to listen to data from the service.
```Java
viewModel
		.getLiveData(Data.class)
		.observe(this, this); //
 ```

Send messages to the service via the `IpcViewModel.dispatchEvent(Parcelable)`

## Extras
###### `List<Parcelable>`
Often the data we want to send is a `List`. But neither `List` nor `ArrayList` are `Parcelable`.
So there's a `ParcelableArrayList.java` ready to use.

Remember that due to Java type erasure, we can't just call `addListener(ParcelableArrayList<MyData>.class, listener)`

So the suggestion is to subclass it, for example:
```Java
public class MyData implements Parcelable {
   int val;
   String otherVal;

   ... parcelable implementation...

   public static List extends ParcelableArrayList<MyData> {
       // override whichever constructor you need
   }
}
```

than you can add a listener to it like:
```Java
ipcConnection.addListener(MyData.List.class, listener);
```


###### IPC while in foreground
If you need to have your service connection active for as long as any of your activities are in foreground, use the `IpcProcessHelper.java`

Also available a `ForegroundDetector.java`, which technically is not related to IPC on itself,
but I wouldn't release it as a separate library.

PS.: If you're using `LiveData`, the architecture components have it built-in. Just use `ProcessLifecycleOwner.get()` as the `LifecycleOwner`

## Gradle
We tried to separate it in many modules as possible, so developers can include just the classes they they really need.
```Java
compile 'com.sensorberg.easyipc:easyipc:0.0.1'              // Core, IpcConnection, Activity n service helpers, etc
compile 'com.sensorberg.easyipc:arch:0.0.1'                 // Android Architectur, IpcLiveData and IpcViewModel
compile 'com.sensorberg.easyipc:foreground:0.0.1'           // IpcProcessHelper
compile 'com.sensorberg.easyipc:foregrounddetector:0.0.1'   // ForegroundDetector
```

#### Feedback and Pull-Requests welcomed.