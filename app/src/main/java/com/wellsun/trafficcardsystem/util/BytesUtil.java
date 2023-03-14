package com.wellsun.trafficcardsystem.util;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;

public class BytesUtil {
    public static int byte2Int(byte b) {
        return b >= 0 ? b : b + 256;
    }

    private static byte hex2byte(char c) {
        if (c <= 'f' && c >= 'a') {
            return (byte) ((c - 'a') + 10);
        }
        if (c <= 'F' && c >= 'A') {
            return (byte) ((c - 'A') + 10);
        }
        if (c > '9' || c < '0') {
            return 0;
        }
        return (byte) (c - '0');
    }

    public static boolean isNeedConvert(char c) {
        return (c & 255) != c;
    }

    public static String bytes2HexString(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (byte b : bArr) {
            String hexString = Integer.toHexString(b & 255);
            if (hexString.length() == 1) {
                sb.append('0');
            }
            sb.append(hexString);
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] intToBytes(long j) {
        byte[] bArr = new byte[4];
        for (int i = 0; i < bArr.length; i++) {
            bArr[i] = (byte) ((int) ((j >> ((3 - i) << 3)) & 255));
        }
        return bArr;
    }

    public static byte[] intTo2Bytes(int i) {
        return subBytes(intToBytes((long) i), 2, 4);
    }

    public static byte[] hexString2Bytes(String str) {
        if (str == null) {
            return null;
        }
        byte[] bArr = new byte[((str.length() + 1) / 2)];
        if ((str.length() & 1) == 1) {
            str = str + "0";
        }
        for (int i = 0; i < bArr.length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((hex2byte(str.charAt(i2)) << 4) | hex2byte(str.charAt(i2 + 1)));
        }
        return bArr;
    }

    public static byte[] subBytes(byte[] bArr, int i) {
        if (i < 0 || bArr.length <= i) {
            return null;
        }
        byte[] bArr2 = new byte[(bArr.length - i)];
        System.arraycopy(bArr, i, bArr2, 0, bArr.length - i);
        return bArr2;
    }

    public static byte[] subBytes(byte[] bArr, int i, int i2) {
        if (i < 0 || bArr.length <= i) {
            return null;
        }
        if (i2 < 0 || bArr.length < i + i2) {
            i2 = bArr.length - i;
        }
        byte[] bArr2 = new byte[i2];
        System.arraycopy(bArr, i, bArr2, 0, i2);
        return bArr2;
    }

    public static byte[] reverseBytes(byte[] bArr) {
        byte[] bArr2 = new byte[bArr.length];
        for (int i = 0; i < bArr.length; i++) {
            bArr2[i] = bArr[(bArr.length - i) - 1];
        }
        return bArr2;
    }

    public static int bytesToInt(byte[] bArr) {
        if (bArr.length > 4) {
            return -1;
        }
        int length = bArr.length - 1;
        int i = 0;
        for (int i2 = 0; i2 < bArr.length; i2++) {
            i |= (bArr[i2] & 255) << ((length - i2) << 3);
        }
        return i;
    }

    public static int twoByteToInt(byte[] bArr) {
        int byte2Int = byte2Int(bArr[0]) + (byte2Int(bArr[1]) * 256);
        Log.i("twoByteToInt", "twoByteToInt: " + byte2Int);
        return byte2Int;
    }

    public static String byte2Str(byte b) {
        String hexString = Integer.toHexString(b & 255);
        if (hexString.length() != 1) {
            return hexString.toUpperCase();
        }
        return ("0" + hexString).toUpperCase();
    }

    public static byte hexStr2Byte(String str) {
        if (str.length() == 1) {
            str = "0" + str;
        }
        return (byte) ((hex2byte(str.charAt(0)) << 4) | hex2byte(str.charAt(1)));
    }

    public static char[] bytes2Chars(byte[] bArr) {
        Charset forName = Charset.forName("UTF-8");
        ByteBuffer allocate = ByteBuffer.allocate(bArr.length);
        allocate.put(bArr);
        allocate.flip();
        return forName.decode(allocate).array();
    }

    public static String bytes3ASCString(byte[] bArr) {
        if (bArr == null) {
            return "";
        }
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b : bArr) {
            stringBuffer.append((char) b);
        }
        return stringBuffer.toString();
    }

    public static byte hexString2Byte(String str) {
        if (str.length() == 1) {
            str = "0" + str;
        }
        return (byte) ((hex2byte(str.charAt(0)) << 4) | hex2byte(str.charAt(1)));
    }

    public static String asciiToString(String str) {
        try {
            return new String(hexString2Bytes(str), "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String stringToAscii(String str) {
        StringBuilder sb = new StringBuilder();
        char[] charArray = str.toCharArray();
        for (int i = 0; i < charArray.length; i++) {
            sb.append(String.format("%02X", Byte.valueOf((byte) charArray[i])));
        }
        return sb.toString().toUpperCase();
    }

    public static byte[] str2Bcd(String str) {
        int i;
        int i2;
        int length = str.length();
        if (length % 2 != 0) {
            str = "0" + str;
            length = str.length();
        }
        byte[] bArr = new byte[length];
        if (length >= 2) {
            length /= 2;
        }
        byte[] bArr2 = new byte[length];
        byte[] bytes = str.getBytes();
        for (int i3 = 0; i3 < str.length() / 2; i3++) {
            int i4 = i3 * 2;
            if (bytes[i4] >= 48 && bytes[i4] <= 57) {
                i = bytes[i4] - 48;
            } else if (bytes[i4] < 97 || bytes[i4] > 122) {
                i = (bytes[i4] - 65) + 10;
            } else {
                i = (bytes[i4] - 97) + 10;
            }
            int i5 = i4 + 1;
            if (bytes[i5] >= 48 && bytes[i5] <= 57) {
                i2 = bytes[i5] - 48;
            } else if (bytes[i5] < 97 || bytes[i5] > 122) {
                i2 = (bytes[i5] - 65) + 10;
            } else {
                i2 = (bytes[i5] - 97) + 10;
            }
            bArr2[i3] = (byte) ((i << 4) + i2);
        }
        return bArr2;
    }

    public static String bcd2Str(byte[] bArr, int i, int i2) {
        StringBuffer stringBuffer = new StringBuffer(bArr.length * 2);
        for (int i3 = i; i3 < i + i2; i3++) {
            stringBuffer.append((int) ((byte) ((bArr[i3] & 240) >>> 4)));
            stringBuffer.append((int) ((byte) (bArr[i3] & 15)));
        }
        return stringBuffer.toString().substring(0, 1).equalsIgnoreCase("0") ? stringBuffer.toString().substring(1) : stringBuffer.toString();
    }

    public static String Unicode2GBK(String str) {
        StringBuffer stringBuffer = new StringBuffer();
        int length = str.length();
        int i = 0;
        while (i < length) {
            if (i < length - 1) {
                int i2 = i + 2;
                if ("\\u".equals(str.substring(i, i2))) {
                    i += 6;
                    stringBuffer.append((char) Integer.parseInt(str.substring(i2, i), 16));
                }
            }
            stringBuffer.append(str.charAt(i));
            i++;
        }
        return stringBuffer.toString();
    }

    public static String utf8ToUnicode(String str) {
        char[] charArray = str.toCharArray();
        StringBuffer stringBuffer = new StringBuffer();
        for (int i = 0; i < str.length(); i++) {
            Character.UnicodeBlock of = Character.UnicodeBlock.of(charArray[i]);
            if (of == Character.UnicodeBlock.BASIC_LATIN) {
                stringBuffer.append(charArray[i]);
            } else if (of == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
                stringBuffer.append((char) (charArray[i] - 65248));
            } else {
                String hexString = Integer.toHexString((short) charArray[i]);
                stringBuffer.append(("\\u" + hexString).toLowerCase());
            }
        }
        return stringBuffer.toString();
    }

    public static byte AddCu(byte[] bArr, int i) {
        long j = 0;
        for (int i2 = 0; i2 < i; i2++) {
            j += ((long) bArr[i2]) >= 0 ? (long) bArr[i2] : ((long) bArr[i2]) + 256;
        }
        return (byte) ((int) j);
    }

    public static byte AddCk(byte[] bArr, int i) {
        long j = 0;
        int i2 = 0;
        while (i2 < i) {
            j += ((long) bArr[i2]) >= 0 ? (long) bArr[i2] : ((long) bArr[i2]) + 256;
            i2++;
        }
        if (((byte) ((int) j)) != bArr[i2]) {
            return 1;
        }
        return 0;
    }

    public static String reserveHexString(String str, int i) {
        int length;
        int i2;
        if (str == null || (i2 = i * 2) < (length = str.length())) {
            return null;
        }
        String str2 = str;
        for (int i3 = 0; i3 < i2 - length; i3++) {
            str2 = "0" + str2;
        }
        byte[] bArr = new byte[((str2.length() + 1) / 2)];
        if ((str2.length() & 1) == 1) {
            str2 = str2 + "0";
        }
        int length2 = bArr.length - 1;
        for (int i4 = 0; i4 <= length2; i4++) {
            int i5 = i4 * 2;
            bArr[length2 - i4] = (byte) ((hex2byte(str2.charAt(i5)) << 4) | hex2byte(str2.charAt(i5 + 1)));
        }
        return bytes2HexString(bArr);
    }

    public static byte hex_bcd(byte b) {
        return (byte) ((b % 10) | ((b / 10) << 4));
    }

    public static byte[] bcdStringToBytes(String str) {
        if (str == null || str.equals("") || str.length() % 2 != 0) {
            return null;
        }
        String upperCase = str.toUpperCase();
        int length = upperCase.length() / 2;
        char[] charArray = upperCase.toCharArray();
        byte[] bArr = new byte[length];
        for (int i = 0; i < length; i++) {
            int i2 = i * 2;
            bArr[i] = (byte) ((charToByte(charArray[i2]) * 10) + charToByte(charArray[i2 + 1]));
        }
        return bArr;
    }

    private static byte charToByte(char c) {
        return (byte) "0123456789ABCDEF".indexOf(c);
    }
}
