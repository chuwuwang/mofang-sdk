# PowerLed

### Interface functions
> Operate LED equipment

### Prototype

```java
void PowerLed(boolean blue, boolean yellow, boolean green, boolean red);
```

- #### Parameter
| Type   | Description         |
| :----- | :------------------ |
| blue   | true:ON ; false:OFF |
| yellow | true:ON ; false:OFF |
| green  | true:ON ; false:OFF |
| red    | true:ON ; false:OFF |


- #### Return
> void


### For example:

```java
try {
     DeviceHelper.getLedDriver().PowerLed(led1.isChecked(), led2.isChecked(), 			led3.isChecked(), led4.isChecked());
} catch (RemoteException e) {
    e.printStackTrace();
}
```



### See also

[Home](../README.md) |[PowerLed](PowerLed.md)|[beep](beep.md)|[printStr](printStr.md)|[printImage](printImage.md)