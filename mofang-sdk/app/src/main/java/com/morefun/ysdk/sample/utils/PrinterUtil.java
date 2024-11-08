package com.morefun.ysdk.sample.utils;

import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;

import com.morefun.yapi.device.printer.FontFamily;

public class PrinterUtil {
    private static final String TAG = "PrinterUtil";
    private final static int ONE_LINE_PRINT_PX = 376;

    public static class TextItem {
        private String text;
        private int font = FontFamily.MIDDLE;
        private int paddingAlign = Gravity.LEFT;
        private int fillSpaceNum = 0;
        private float pxSize = 0;

        public TextItem(String text) {
            this.text = text;
        }

        public TextItem setFont(int font) {
            this.font = font;
            return this;
        }

        public TextItem setPaddingAlign(int align) {
            this.paddingAlign = align;
            return this;
        }

        public TextItem setFillSpaceNum(int num) {
            Log.d(TAG, "setFillSpaceNum:" + num);
            this.fillSpaceNum = num;
            return this;
        }

        public String getText() {
            if (text == null) {
                return "";
            }

            if (getAlign() == Gravity.RIGHT) {
                text = addPadding(text, true, ' ', fillSpaceNum);
            } else if (getAlign() == Gravity.FILL_HORIZONTAL) {
                text = addPadding(text, false, ' ', fillSpaceNum);
            }
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public String getOriginalText() {
            return this.text;
        }

        public int getFont() {
            return font;
        }

        public int getAlign() {
            return paddingAlign;
        }

        public float getPxSize() {
            return pxSize;
        }

        public TextItem setPxSize(float pxSize) {
            this.pxSize = pxSize;
            return this;
        }
    }

    private static String addPadding(String src, boolean isLeft, char padding, int fixLen) {
        StringBuilder b = new StringBuilder();

        for (int i = 0; i < fixLen; ++i) {
            b.append(padding);
        }

        if (isLeft) {
            b.append(src);
        } else {
            b.insert(0, src);
        }

        return b.toString();
    }

    public static String makeLineText(TextItem... textItems) {
        String text = "";
        float textPX = 0;
        float totalPX = 0;

        for (TextItem textItem : textItems) {
            textPX = measureTextPX(textItem.getText(), textItem.font);
            totalPX += textPX;
            if (textItem.getAlign() == Gravity.RIGHT) {
                int fillSpaceNum = 0;
                if (textItem.getFont() == FontFamily.BIG) {
                    fillSpaceNum = (int) (ONE_LINE_PRINT_PX - totalPX) / 8;
                } else if (textItem.getFont() == FontFamily.SMALL) {
                    fillSpaceNum = (int) (ONE_LINE_PRINT_PX - totalPX) / 4;
                } else {
                    fillSpaceNum = (int) (ONE_LINE_PRINT_PX - totalPX) / 6;
                }
                textItem.setFillSpaceNum(fillSpaceNum);
            } else if (textItem.getAlign() == Gravity.FILL_HORIZONTAL) {
                if (textItem.getPxSize() > textPX) {
                    int fillSpaceNum = 0;
                    if (textItem.getFont() == FontFamily.BIG) {
                        fillSpaceNum = (int) (textItem.getPxSize() - textPX) / 8;
                    } else if (textItem.getFont() == FontFamily.SMALL) {
                        fillSpaceNum = (int) (textItem.getPxSize() - textPX) / 4;
                    } else {
                        fillSpaceNum = (int) (textItem.getPxSize() - textPX) / 6;
                    }
                    textItem.setFillSpaceNum(fillSpaceNum);
                }
            }
            text += textItem.getText();
        }
        return text;
    }

    private static float measureTextPX(String text, int font) {
        Paint paint = new Paint();
        if (font == FontFamily.BIG) {
            paint.setTextSize(32);
        } else if (font == FontFamily.SMALL) {
            paint.setTextSize(16);
        } else {
            paint.setTextSize(24);
        }
        Log.d(TAG, "text:" + text);
        Log.d(TAG, "text px:" + paint.measureText(text));
        return paint.measureText(text);
    }
}
