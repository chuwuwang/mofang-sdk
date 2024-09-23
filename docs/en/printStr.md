# printStr

### Function functions
> MultipleAppPrinter Start print  text and bitmap.

### Prototype

```java
int printStr(in List<MulPrintStrEntity> lists,in OnPrintListener
    listener, in Bundle config);
```

- #### Parameter
| Name     | Type                                | Description                                              |
| :------- | :---------------------------------- | :------------------------------------------------------- |
| lists    | List<MulPrintStrEntity>             | see[MulPrintStrEntity](#MulPrintStrEntity)               |
| listener | [OnPrintListener](#OnPrintListener) | print result callback                                    |
| config   | Bundle                              | print config ,see [PrinterConfig](enum.md#PrinterConfig) |


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

- #### MulPrintStrEntity

```
	public class MulPrintStrEntity implements Parcelable {
    private String text;
    private Bitmap bitmap;
    private int fontsize;
    private int isBold;
    // 0 - 60
    private int yspace;
    //
    private boolean underline;
    private int marginX;
    private int gravity = Gravity.START;

    public MulPrintStrEntity(String text, int fontsize) {
        this.text = text;
        this.fontsize = fontsize;
    }

    public MulPrintStrEntity(String text, int fontsize, int isBold) {
        this.text = text;
        this.fontsize = fontsize;
        this.isBold = isBold;
    }

    public MulPrintStrEntity(String text, int fontsize, int isBold, int yspace) {
        this.text = text;
        this.fontsize = fontsize;
        this.isBold = isBold;
        this.yspace = yspace;
    }

    public MulPrintStrEntity(String text, int fontsize, boolean underline, int gravity) {
        this.text = text;
        this.fontsize = fontsize;
        this.underline = underline;
        this.gravity = gravity;
    }

    public MulPrintStrEntity() {
    }

    protected MulPrintStrEntity(Parcel in) {
        text = in.readString();
        fontsize = in.readInt();
        isBold = in.readInt();
        yspace = in.readInt();
        underline = in.readByte() != 0;
        marginX = in.readInt();
        gravity = in.readInt();
        bitmap = in.readParcelable(Bitmap.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeInt(fontsize);
        dest.writeInt(isBold);
        dest.writeInt(yspace);
        dest.writeByte((byte) (underline ? 1 : 0));
        dest.writeInt(marginX);
        dest.writeInt(gravity);
        dest.writeParcelable(bitmap, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MulPrintStrEntity> CREATOR = new Creator<MulPrintStrEntity>() {
        @Override
        public MulPrintStrEntity createFromParcel(Parcel in) {
            return new MulPrintStrEntity(in);
        }

        @Override
        public MulPrintStrEntity[] newArray(int size) {
            return new MulPrintStrEntity[size];
        }
    };

    public String getText() {
        return text;
    }

    public MulPrintStrEntity setText(String text) {
        this.text = text;
        return this;
    }

    public int getFontsize() {
        return fontsize;
    }

    public MulPrintStrEntity setFontsize(int fontsize) {
        this.fontsize = fontsize;
        return this;
    }

    public int getIsBold() {
        return isBold;
    }

    public MulPrintStrEntity setIsBold(int isBold) {
        this.isBold = isBold;
        return this;
    }

    public int getYspace() {
        return yspace;
    }

    public MulPrintStrEntity setYspace(int yspace) {
        this.yspace = yspace;
        return this;
    }

    public boolean isUnderline() {
        return underline;
    }

    public MulPrintStrEntity setUnderline(boolean underline) {
        this.underline = underline;
        return this;
    }

    public int getMarginX() {
        return marginX;
    }

    public MulPrintStrEntity setMarginX(int marginX) {
        this.marginX = marginX;
        return this;
    }

    public int getGravity() {
        return gravity;
    }

    public MulPrintStrEntity setGravity(int gravity) {
        this.gravity = gravity;
        return this;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
```





### See also

[Home](../README.md) |[PowerLed](PowerLed.md)|[beep](beep.md)|[printStr](printStr.md)|[printImage](printImage.md)

