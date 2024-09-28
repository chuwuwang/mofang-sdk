package com.morefun.ysdk.sample.utils;

import android.text.TextUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class BytesUtil {
    private static final String CHARSET_ISO8859_1 = "ISO-8859-1";
    private static final String CHARSET_GBK = "GBK";
    private static final String CHARSET_GB2312 = "GB2312";
    private static final String CHARSET_UTF8 = "UTF-8";

    private BytesUtil() {
    }

    public static int int2bytes(int d, byte[] outdata, int offset) {
        outdata[offset + 3] = (byte) ((d >> 24) & 0xff);
        outdata[offset + 2] = (byte) ((d >> 16) & 0xff);
        outdata[offset + 1] = (byte) ((d >> 8) & 0xff);
        outdata[offset + 0] = (byte) ((d >> 0) & 0xff);
        return offset + 4;
    }

    public static String bytes2HexString(byte[] data) {
        if (data == null) {
            return "";
        }
        StringBuilder buffer = new StringBuilder();
        byte[] var5 = data;
        int var4 = data.length;

        for (int var3 = 0; var3 < var4; ++var3) {
            byte b = var5[var3];
            String hex = Integer.toHexString(b & 255);
            if (hex.length() == 1) {
                buffer.append('0');
            }

            buffer.append(hex);
        }

        return buffer.toString().toUpperCase();
    }

    public static byte[] hexString2Bytes(String data) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        byte[] result = new byte[(data.length() + 1) / 2];
        if ((data.length() & 1) == 1) {
            data = data + "0";
        }

        for (int i = 0; i < result.length; ++i) {
            result[i] = (byte) (hex2byte(data.charAt(i * 2 + 1)) | hex2byte(data.charAt(i * 2)) << 4);
        }

        return result;
    }

    public static byte hex2byte(char hex) {
        return hex <= 102 && hex >= 97 ? (byte) (hex - 97 + 10) : (hex <= 70 && hex >= 65 ? (byte) (hex - 65 + 10) : (hex <= 57 && hex >= 48 ? (byte) (hex - 48) : 0));
    }

    public static byte[] subBytes(byte[] data, int offset, int len) {
        if (data == null) {
            return null;
        }
        if (offset >= 0 && data.length > offset) {
            if (len < 0 || data.length < offset + len) {
                len = data.length - offset;
            }

            byte[] ret = new byte[len];
            System.arraycopy(data, offset, ret, 0, len);
            return ret;
        } else {
            return null;
        }
    }

    public static byte[] merage(byte[]... data) {
        if (data == null) {
            return null;
        }
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        try {
            byte[][] var5 = data;
            int var4 = data.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                byte[] e = var5[var3];
                if (e == null) {
                    throw new IllegalArgumentException("");
                }

                buffer.write(e);
            }

            byte[] var7 = buffer.toByteArray();
            return var7;
        } catch (IOException var15) {
            var15.printStackTrace();
        } finally {
            try {
                buffer.close();
            } catch (IOException var14) {
                var14.printStackTrace();
            }

        }

        return null;
    }

    /**
     * 将多个字节数组按顺序合并
     *
     * @param data
     * @return
     */
    public static byte[] byteArraysToBytes(byte[][] data) {

        int length = 0;
        for (int i = 0; i < data.length; i++) {
            length += data[i].length;
        }
        byte[] send = new byte[length];
        int k = 0;
        for (int i = 0; i < data.length; i++) {
            for (int j = 0; j < data[i].length; j++) {
                send[k++] = data[i][j];
            }
        }
        return send;
    }

    public static int bytesToInt(byte[] bytes) {
        if (bytes.length > 4) {
            return -1;
        } else {
            int lastIndex = bytes.length - 1;
            int result = 0;

            for (int i = 0; i < bytes.length; ++i) {
                result |= (bytes[i] & 255) << (lastIndex - i << 3);
            }

            return result;
        }
    }

    public static int littleEndianBytesToInt(byte[] bytes) {
        if (bytes.length > 4) {
            return -1;
        } else {
            int result = 0;

            for (int i = 0; i < bytes.length; ++i) {
                result |= (bytes[i] & 255) << (i << 3);
            }

            return result;
        }
    }

    public static byte[] intToBytes(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) (intValue >> (3 - i << 3) & 255);
        }

        return bytes;
    }

    public static byte[] intToLittleEndianBytes(int intValue) {
        byte[] bytes = new byte[4];

        for (int i = 0; i < bytes.length; ++i) {
            bytes[i] = (byte) (intValue >> (i << 3) & 255);
        }

        return bytes;
    }

    public static String bcd2Ascii(byte[] bcd) {
        if (bcd == null) {
            return "";
        } else {
            StringBuilder sb = new StringBuilder(bcd.length << 1);
            byte[] var5 = bcd;
            int var4 = bcd.length;

            for (int var3 = 0; var3 < var4; ++var3) {
                byte ch = var5[var3];
                byte half = (byte) (ch >> 4);
                sb.append((char) (half + (half > 9 ? 55 : 48)));
                half = (byte) (ch & 15);
                sb.append((char) (half + (half > 9 ? 55 : 48)));
            }

            return sb.toString();
        }
    }

    public static byte[] ascii2Bcd(String ascii) {
        if (ascii == null) {
            return null;
        } else {
            if ((ascii.length() & 1) == 1) {
                ascii = "0" + ascii;
            }

            byte[] asc = ascii.getBytes();
            byte[] bcd = new byte[ascii.length() >> 1];

            for (int i = 0; i < bcd.length; ++i) {
                bcd[i] = (byte) (hex2byte((char) asc[2 * i]) << 4 | hex2byte((char) asc[2 * i + 1]));
            }

            return bcd;
        }
    }

    public static byte[] toBytes(String data, String charsetName) {
        if (TextUtils.isEmpty(data)) {
            return null;
        }
        try {
            return data.getBytes(charsetName);
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static byte[] toBytes(String data) {
        return toBytes(data, "ISO-8859-1");
    }

    public static byte[] toGBK(String data) {
        return toBytes(data, "GBK");
    }

    public static byte[] toGB2312(String data) {
        return toBytes(data, "GB2312");
    }

    public static byte[] toUtf8(String data) {
        return toBytes(data, "UTF-8");
    }

    public static String fromBytes(byte[] data, String charsetName) {
        try {
            return new String(data, charsetName);
        } catch (UnsupportedEncodingException var3) {
            return null;
        }
    }

    public static String fromBytes(byte[] data) {
        return fromBytes(data, "ISO-8859-1");
    }

    public static String fromGBK(byte[] data) {
        return fromBytes(data, "GBK");
    }

    public static String fromGB2312(byte[] data) {
        return fromBytes(data, "GB2312");
    }

    public static String fromUtf8(byte[] data) {
        return fromBytes(data, "UTF-8");
    }

    /**
     * 排头字节放到排尾
     * e.g.
     * before:1122334455667788
     * after:2233445566778811
     *
     * @param rbuf
     * @return
     */
   public static byte[] insertFirst2Last(byte[] rbuf) {
        byte temp;
        byte tmp[] = new byte[8];

        for (int i = 0; i < 8; i++) {
            temp = rbuf[0];
            for (int j = 0; j < 7; j++) {
                tmp[j] = rbuf[j + 1];
            }
            tmp[7] = temp;
        }
        return tmp;

    }

    /**
     * 排尾字节放到排头
     * e.g.
     * before:1122334455667788
     * after:8811223344556677
     *
     * @param rbuf
     * @return
     */
   public static byte[] insertLast2First(byte[] rbuf) {
        char temp;
        byte tmp[] = new byte[8];
        tmp[0] = rbuf[7];
        for (int j = 1; j < 8; j++) {
            tmp[j] = rbuf[j - 1];
        }
        return tmp;
    }
}