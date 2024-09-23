### [20210120]
1.update readChipReader& readContactlessReader
2.add load capk return Emv_CAPK_HASH_INCORRECT.
3.AID support 50,87
### [20210105]
​	Version-> 3.9.0
DispCard / SelectApp Add terminate the EMV process

### [20201208]
​	Version-> 3.8.6
1.addCertRevokedList
2.clearCertRevokedList
3.Update EMV kernel

### [20201116]
​	Version-> 3.8.3
1.Add onFinish result
Emv_TRY_AGAIN
Emv_TRY_OTHER_PAGE


### [20201023]

​	Version-> 3.8.1

1. support offline random keynum


### [20201022]

​	Version-> 3.8.1

1. Fix: IC Card can’t be swipe .
2. EMV Kernel contactless unified tag (Rupay -> 9F1B -> DF19)

### [20201014]

1. remove read phone Permission
2. Optimize non-contact process and shorten time

### [20200925]

1. refactor and add USerialPort module

### [20200923]

1. CBC -> IV
2. Contactless onFinish callback value [ Bundle.putInt(EmvErrorConstrants.EMV_GOTO_CODE, value) ]
    -12: Mobile device
   -11: Card
   -13 see phone

### [20200915]

1. PrinterConfig -> add Params-> TextView direction

   Support R->L .etc.

2. Fix onSelApp crash (make by 20200806)

3.  Add some MacAlgorithm 

### [20200821]

1. SDK  Version -> 3.5

2. PinPad Dialog Style add white.

3. addAidParam -> support DF8118  DF8119

4.  refactor  addAidParam 

5.  PinPad:

   int desLoad(in DesLoadObj desLoadObj);

   boolean checkKey(in TDesKeyObj tDesKeyObj);

### [20200731]

1. Multi AIDs

2. PinPad add Api:

   byte[] desCalcByKey(in DesCalcObj desCalcObj);

### [20200728]

1. CPUCardHandler add API

   int exchangeCmd(inout byte[]result, in byte[]cmd, in int cmdlen)

2.  fix Force pin  bug.

### [20200602]

1. Update Bypass callback, length changed from 6 to 0 byte array
2. Update the logic of forced online input pin, if offline Pin is supported, after offline Pin, the forced online Pin will also be input

### [20200428]

1. add custom tag 6B to get track 2
2. Refactored readEmvData interface

### [20200423]

1. Fixed a crash with a low probability of initialization.
2. Optimized card detection logic.

### [20200313]

1. Support  Rupay Service

#### [20200206]

1.change OnEmvProcessListener:
Add Rupay contactless (double tap、Long tag) case.



2.EmvTransDataConstrants.CONTACTLESS_PIN_FREE_AMT
Pin Free Amount for contactLess(Amount 2000).



#### [20200116]

1.change inputText API.

2.cashback support.

3.Add more 9C support (EMVTag9CConstants) .



#### [20200109-dev]

1.fix bypass value(new byte[0]).
2.add custom tag.
3.change input online pin length( 4 position).



#### [20200103-dev]

1.Add Grad logcat switch.
2.Add jude:
  if the amount of connect less transactions is more than 2,000. The interface prompts you to swipe or insert a card.
3.EmvTransDataConstrants.FORCE_ONLINE_CALL_PIN:
For online transactions, the terminal must force to enter the password,Please set true.



#### [20191225]

1、Dukpt：At most 6 groups of keys are supported.
2、Dukpt：increaseKSN API : Generate new PEK and return new KSN.

