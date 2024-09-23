# addAidParam

### Interface functions
> Add a EMV Parameter

### Prototype

```java
int addAidParam(byte[] aidPara);
```

- #### Parameter
| Type   | Description                |
| :----- | :------------------------- |
| byte[] | AID parameter(Format: TLV) |


- #### Return
| Type | Description |
| :--- | :---------- |
| 0    | Succeed     |
| else | Fail        |

### AID Details:

```
9F06(07)    <T> Terminal Application Identifier
9F09(02)   <T> Application Version Number
DF11(05)   <T> terminal Action Code-Default
DF12(05)  <T> terminal Action Code-Online
DF13(05)  <T> terminal Action Code-Denial
9F1B(04)  <T> Terminal Floor Limit
5F2A(02)  <T> Transaction Currency Code
5F36(01)  <T> Transaction Currency Exponent
DF19(06)  <T> Contactless Floor Limit
DF20(06)  <T> Contactless Transaction Limit
DF21(06)  <T> Contactless CVM Limit
DF17(01)  <T> Target Percentage for Random Selection
DF16(01)  <T> Maximum Target Percentage for Random Selection
DF15(04)  <T> Threshold Value for Biased Random Selection
50(16)   <T>Application Label, e.g.(0xA0 0x00 0x00 0x00 0x03 0x10 0x10 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00)
DF01(01)  <T> Application selection indicator,0 or 1, e.g.(0x00)
DF18(01)  <T> Terminal online pin capability, 0x30 or 0x31 , e.g.(0x31)
DF14(252)  <T> Default DDOL(Hex), e.g.(0x9F 0x37 0x04 0x00 ...)
9F7B(06)  <T> EC Terminal Transaction Limit, e.g.(0x00 0x00 0x00 0x00 0x20 0x00)--for UnionPay cards
```
The method of setting aid is like the way provided by demo.

Contactless Transaction Limit : If the limit is exceeded, the transaction will fail.

Contactless Floor Limit            :  If the limit is exceeded, the transaction may request online.

Contactless CVM Limit             :  If the limit is exceeded, the transaction will request CVM method.



### See also

[Home](../README.md)|[emvProcess](emvProcess.md) |[readEmvData](readEmvData.md)|[initTermConfig](initTermConfig.md)|[addAidParam](addAidParam.md)|[addCAPKParam](addCAPKParam.md)