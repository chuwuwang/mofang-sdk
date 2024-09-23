# addCAPKParam

### Interface functions
> Add a CA public key

### Prototype

```java
int addCAPKParam(byte[] capkParam);
```

- #### Parameter
| Type   | Description                     |
| :----- | :------------------------------ |
| byte[] | CA public key list(Format: TLV) |


- #### Return
| Type | Description |
| :--- | :---------- |
| 0    | Succeed     |
| else | Fail        |

### CAPK Details:

```
9f06 Registered Application Provider Identifier (RID)
9F22 Certification Authority Public Key Index
DF06 Certification Authority Hash Algorithm Indicator
DF07 Certification Authority Public Key Algorithm Indicator
DF02 Certification Authority Public Key Modulus
DF04 Certification Authority Public Key Exponent
DF05 Certification Authority Public Key Expiration date
DF03 Certification Check Sum
```




### See also

[Home](../README.md)|[emvProcess](emvProcess.md) |[readEmvData](readEmvData.md)|[initTermConfig](initTermConfig.md)|[addAidParam](addAidParam.md)|[addCAPKParam](addCAPKParam.md)

