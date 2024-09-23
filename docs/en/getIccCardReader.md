# getIccCardReader

### Interface functions
> Get ICCard Reader controlling object.

### Prototype

```java
IccCardReader getIccCardReader(int slotNo) throws RemoteException;
```

- #### Parameter
| Type | Description                               |
| :--- | :---------------------------------------- |
| int  | see[IccReaderSlot](enum.md#IccReaderSlot) |


- #### Return
| Type          | Description          |
| :------------ | :------------------- |
| IccCardReader | IccCardReader object |


### For example:

```java
//Contact
    final IccCardReader icReader = DeviceHelper.getIccCardReader(IccReaderSlot.ICSlOT1);
//Contactless
 	final IccCardReader rfReader = DeviceHelper.getIccCardReader(IccReaderSlot.RFSlOT);
```



### See also

[Home](../README.md) |[getIccCardReader](getIccCardReader.md)|[searchCard](searchCard.md)