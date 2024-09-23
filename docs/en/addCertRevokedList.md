# addCertRevokedList

### Interface functions
> Add  cert revoked list

### Prototype

```java
int addCertRevokedList(byte[] certRevoked);
```

- #### Parameter
| Type   | Description                |
| :----- | :------------------------- |
| byte[] | Cert Revoked (Format: TLV) |


- #### Return
| Type | Description |
| :--- | :---------- |
| 0    | Succeed     |
| else | Fail        |



### See also

[Home](../README.md)|[emvProcess](emvProcess.md) |[readEmvData](readEmvData.md)|[initTermConfig](initTermConfig.md)|[addAidParam](addAidParam.md)|[addCAPKParam](addCAPKParam.md)