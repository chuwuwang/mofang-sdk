# readEmvData

### Function functions
> Get Emv Tags value.

### Prototype

```java
int readEmvData(in String[] taglist,out byte[] buffer ,inout Bundle bundle);
```

- #### Parameter
| Name    | Type   | Description   |
| :------ | :----- | :------------ |
| taglist | int    | Emv tag list. |
| buffer  | byte[] | output data.  |
| bundle  | Bundle | With dukpt.   |


- #### Return
| Value | Description |
| :---- | :---------- |
| 0     | Succeed     |
| else  | Fail        |


### See also

[Home](../README.md)|[emvProcess](emvProcess.md) |[readEmvData](readEmvData.md)|[initTermConfig](initTermConfig.md)|[addAidParam](addAidParam.md)|[addCAPKParam](addCAPKParam.md)

