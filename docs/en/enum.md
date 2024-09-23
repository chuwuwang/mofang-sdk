
### DeviceInfoConstrants

| Name               | Value           | Description               | **Bundle value** |
| ------------------ | --------------- | ------------------------- | ---------------- |
| COMMOM_VENDOR      | "vendor"        | morefun                   | String           |
| COMMOM_MODEL       | "model"         | Model                     | String           |
| COMMOM_OS_VER      | "os_ver"        | System version            | String           |
| COMMOM_SN          | "sn"            | Serial No                 | String           |
| COMMON_SERVICE_VER | "service_ver"   | SDK  version              | String           |
| IS_COMM_SIG        | "is_common_sig" | "true": apk need to sign. | String           |
| COMMOM_HARDWARE    | "hardware"      | Basechip version.         | String           |


### MacAlgorithmType

| Name | Value | Description |
| ---- | ----- | ----------- |
| ECB  | 0     | mac key     |
| CBC  | 1     | track key   |



### PinPadConstrants

| Name                    | Key                       | Description                         | **Bundle value** |
| ----------------------- | ------------------------- | ----------------------------------- | ---------------- |
| IS_SHOW_TITLE_HEAD      | "show_title_head"         | Password layout title head show.    | boolean          |
| TITLE_HEAD_CONTENT      | "title_head_content"      | Password layout content.            | String           |
| IS_SHOW_PASSWORD_BOX    | "show_password_box"       | Password layout password view.      | boolean          |
| COMMON_NEW_LAYOUT       | "common_new_layout"       | New Password layout                 | boolean          |
| COMMON_OK_TEXT          | "common_ok_text"          | Button OK Text                      | String           |
| COMMON_CANCEL_TEXT      | "common_cancel_text"      | Button Cancel Text                  | String           |
| COMMON_DELETE_TEXT      | "common_delete_text"      | Button Delete Text(Need new layout) | String           |
| COMMON_SUPPORT_KEYVOICE | "common_support_KeyVoice" | Key tone                            | boolean          |
| COMMON_USE_APKEY        | "common_use_apkey"        | use AP Key Encrypt Pinblock         | boolean          |
| COMMON_SHIELD_RETURN    | "common_shield_return"    | Shield return key                   | boolean          |
| COMMON_IS_RANDOM        | "common_is_random"        | Are the number keys random          | boolean          |
| COMMON_LOCK_STATUS_BAR  | "common_lock_statusBar"   | Lock status bar                     | boolean          |



### ServiceResult

| Parameter           | Value | Description                |
| ------------------- | ----- | -------------------------- |
| PinPad_Input_Cancel | -7006 | PinPad Input Cancel button |
| PinPad_Input_OK     | -7044 | PinPad Input Okay button   |
| PinPad_Input_Clear  | -7035 | PinPad Input Clear button  |
| PinPad_Input_Num    | -7045 | PinPad Input Number        |
| Param_In_Invalid    | -2 | Method Param Invalid        |
| PinPad_Dstkey_Idx_Error    | -7012 | Load key Index error     |
| PinPad_Key_Len_Error    | -7014 | Load key Index error        |
| PinPad_No_Key_Error    | -7001 | Master key is empty         |
| ServiceResult | 0 | Successful transaction or Returns the success of the value |
| Emv_FallBack | -8014 | Emv Fallback |
| Emv_Terminate | -8022 | Emv Transaction Terminate |
| Emv_TryOtherPage | -8023 | Emv try other page |
| Emv_TryAgain | -8024 | Emv try Again  (Master need support, GOTO_TRY_AGAIN_SEE_PHONE,GOTO_TRY_AGAIN_MOBILE,GOTO_TRY_AGAIN_CARD) |
| Emv_Declined | -8021 | Emv declined |
| Emv_Cancel | -8020 | Emv cancel |

### WorkKeyType

| Name   | Value  | Description |
| ------ | ------ | ----------- |
| PINKEY | 0(int) | Pin key.    |
| MACKEY | 1(int) | Mac key.    |
| TDKEY  | 2(int) | Track key.  |

### EMVTermCfgConstrants

| Name            | Value               | Description                                                  | **Bundle value** |
| --------------- | ------------------- | ------------------------------------------------------------ | ---------------- |
| TERMCAP         | "termCap"           | Indicates the card data input, CVM, and security capabilities of the Terminal.(9F33) | byte[3]          |
| TRANS_PROP_9F66 | "trans_prop_9F66"   | Terminal Transaction Qualifiers:0x3600c000                   | byte[5]          |
| TERMTYPE        | "termType"          | Terminal type(9F35):0x22                                     | Byte             |
| COUNTRYCODE     | "countryCode"       | Country code(9F1A)                                           | byte[2]          |
| CURRENCYCODE    | "curCode"           | Currency code(5F2A)                                          | byte[9]          |
| ADDTERMCAP      | "additionalTermCap" | Terminal addition capabilities (9F40)                        | byte[5]          |
| TERMID          | "termId"            | Terminal Identification(9F1C)                                | byte[9]          |
| FORCEONLINE     | "forceOnline"       | Foce online                                                  | boolean          |

#### Note: Others that do not appear in the table are reserved attributes.




### **EMVTransDataConstrants**

| Name                  | Value           | Description                                                  | **Bundle value**  |
| --------------------- | --------------- | ------------------------------------------------------------ | ----------------- |
| PROCTYPE              | procType        | 0:Full process,1:SIMPLE process,2:QPASS process              | int               |
| CHANNELTYPE           | channelType     | Contact : 0, Contactless : 1                                 | int               |
| SEQNO                 | posSer          | POS batch number                                             | String            |
| TRANSAMT              | transAmt        | transaction amount                                           | String            |
| CASHBACKAMT           | cashbackAmt     | Cashback amount                                              | String            |
| TRANSDATE             | transDate       | transaction date(YYYYMMDD)                                   | String            |
| TRANSTIME             | transTime       | transaction time(hhmmss)                                     | String            |
| MERNAME               | merName         | Merchant name                                                | String            |
| MERID                 | merId           | Merchant number                                              | String            |
| B9C                   | 9C              | Transaction Type refer DES.1(ISO 8583).                      | byte              |
| ISQPBOCFORCE          | isQpbocForceLin | Whether Qpboc forced                                         | boolean           |
| TERMINAL_TLVS         | TerminalTlvs    | EMV terminal tlvs Such as DF81190118                         | ArrayList<String> |
| FORCE_ONLINE_CALL_PIN | ForceOnlinePin  | If there is no online pin callback, an online pin callback is called back | boolean           |


### ICCSearchResult

| Name      | Value       | Description              |
| --------- | ----------- | ------------------------ |
| CARDTYPE  | "CardType"  | Card type                |
| M1SN      | “M1_sn"     | M1 card sn               |
| CARDOTHER | "CardOther" | Contact:1; Contactless:7 |

### IccCardType

| Name      | Value       | Description |
| --------- | ----------- | ----------- |
| CPUCARD   | "CPUCARD"   | CPU card    |
| AT24CXX   | "AT24CXX"   | CPU card    |
| AT88SC102 | "AT88SC102" | CPU card    |
| M1CARD    | "M1CARD"    | M1 Card     |
| PSAM      | "PSAM"      | PSAM        |

### IccReaderSlot

| Name      | Value  | Description |
| --------- | ------ | ----------- |
| ICSlOT1   | 1(int) | Contact     |
| PSAMSlOT1 | 4(int) | PSAM        |
| RFSlOT    | 7(int) | Contactless |

### BeepModeConstrants

| Name    | Value  | Description   |
| ------- | ------ | ------------- |
| NORAML  | 0(int) | Beeps once    |
| SUCCESS | 1(int) | Beeps ok      |
| FAIL    | 2(int) | Beeps failure |

### PrinterConfig

| Name                 | Value          | Description        | **Bundle value** |
| -------------------- | -------------- | ------------------ | ---------------- |
| COMMON_GRAYLEVEL     | "graylevel"    | Gray level(10-40)  | int              |
| COMMON_TYPEFACE      | "typeface"     | typeface String    | String           |
| COMMON_TYPEFACE_PATH | "typefacePath" | typeface file path | String           |

### EmvOnlineRequest
| Name                 | Value          | Description        | **Bundle value** |
| -------------------- | -------------- | ------------------ | ---------------- |
| CVM_FLAG | "cvmFlag" | [CVM_FLAG](#CVM_FLAG) | int              |
| CVM_SIGNATURE | "cvm_signature" | true:Signature required. | Boolean |

### CVM_FLAG
| Value                 | Description          |
| -------------------- | -------------- |
| 0x00  |No CVM verification required |
| 0x01  |Offline Pin |
| 0x02  |Online Pin |
| 0x03  |signature |
| 0x04  |Online pin plus signature |
| 0x05  |On device cardholder verification |