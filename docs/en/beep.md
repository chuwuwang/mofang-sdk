# beep

### Interface functions
> Operate buzzer equipment

### Prototype

```java
void beep(int mode);
```

- #### Parameter
| Type | Description                                              |
| :--- | :------------------------------------------------------- |
| mode | see [**BeepModeConstrants**](enum.md#BeepModeConstrants) |


- #### Return
> void


### For example:

```java
int beepType;
        try {
            if (normal.isChecked()) {
                beepType = NORMAL;
            } else if (success.isChecked()) {
                beepType = SUCCESS;
            } else if (fail.isChecked()) {
                beepType = FAIL;
            } else if (interval.isChecked()) {
                beepType = INTERVAL;
            } else {
                beepType = ERROR;
            }

            DeviceHelper.getBeeper().beep(beepType);

        } catch (RemoteException e) {
            e.printStackTrace();
        }
```



### See also

[Home](../README.md) |[PowerLed](PowerLed.md)|[beep](beep.md)|[printStr](printStr.md)|[printImage](printImage.md)