# printImage

### Function functions
> MultipleAppPrinter Start print  image.

### Prototype

```java
int printImage(in Bitmap bitmap, in OnPrintListener listener, in Bundle config);
```

- #### Parameter
| Name     | Type                                | Description                                              |
| :------- | :---------------------------------- | :------------------------------------------------------- |
| bitmap   | Bitmap                              | image                                                    |
| listener | [OnPrintListener](#OnPrintListener) | print result callback                                    |
| config   | Bundle                              | print config, see [PrinterConfig](enum.md#PrinterConfig) |


- #### Return
| Value | Description |
| :---- | :---------- |
| 0     | Succeed     |
| else  | Fail        |

### OnPrintListener

```
void onPrintResult(int retCode);
```

- #### Parameter
| Name    | Type | Description |
| :------ | :--- | :---------- |
| retCode | int  | 0: succeed  |

- #### Return

  > void




### See also

[Home](../README.md) |[PowerLed](PowerLed.md)|[beep](beep.md)|[printStr](printStr.md)|[printImage](printImage.md)

