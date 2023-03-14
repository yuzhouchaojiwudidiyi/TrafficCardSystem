package com.wellsun.trafficcardsystem.pboc;

/**
 * date     : 2023-03-13
 * author   : ZhaoZheng
 * describe :
 */

import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.Scanner;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

public class PBOCUtil {
    public static String getMac2(String data, String processKey, String icv)
            throws Exception {
        byte[] processKeyBytes = hexString2byte(processKey);
        String mac2Data = data;
        byte[] icvBytes = hexString2byte(icv);
        byte[] mac2DataBytes = hexString2byte(mac2Data);
        byte[] calcPbocdesMACBytes = calculatePbocdesMAC(mac2DataBytes, processKeyBytes, icvBytes);
        String calcPbocdesMac = byte2hexString(calcPbocdesMACBytes);
        String mac2 = calcPbocdesMac.substring(0, 8);
        return mac2;
    }

    //计算过程密匙
    public static String getDes(String data, String key)
            throws Exception {
        byte[] processKeyBytes = hexString2byte(key);
        byte[] randomDataBytes = hexString2byte(data);
        byte[] calcPbocdesBytes = calculatePbocdes(randomDataBytes, processKeyBytes);
        String calcPbocdes = byte2hexString(calcPbocdesBytes);
        String des = calcPbocdes.substring(0, 16);
        return des;
    }

    private static byte[] calculatePbocdes(byte[] randomDataBytes, byte[] keyBytes) throws Exception {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(keyBytes);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(1, secretKey, sr);
        return cipher.doFinal(randomDataBytes);
    }

    public static byte[] calculatePbocdesMAC(byte[] data, byte[] key, byte[] icv)
            throws Exception {
        if ((key == null) || (data == null))
            throw new RuntimeException("data or key is null.");
        if (key.length != 8) {
            throw new RuntimeException("key length is not 16 byte.");
        }
        int dataLength = data.length;
        int blockCount = dataLength / 8 + 1;
        int lastBlockLength = dataLength % 8;
        byte[][] dataBlock = new byte[blockCount][8];
        for (int i = 0; i < blockCount; i++) {
            int copyLength = i == blockCount - 1 ? lastBlockLength : 8;
            System.arraycopy(data, i * 8, dataBlock[i], 0, copyLength);
        }
        dataBlock[(blockCount - 1)][lastBlockLength] = -128;
        byte[] desXor = xOr(dataBlock[0], icv);
        for (int i = 1; i < blockCount; i++) {
            byte[] des = encryptByDesCbc(desXor, key);
            desXor = xOr(dataBlock[i], des);
        }
        desXor = encryptByDesCbc(desXor, key);
        return desXor;
    }

    public static byte[] encryptByDesCbc(byte[] content, byte[] key)
            throws GeneralSecurityException {
        byte[] ZERO_IVC = new byte[8];
        return encryptByDesCbc(content, key, ZERO_IVC);
    }

    public static byte[] encryptByDesCbc(byte[] content, byte[] key, byte[] icv)
            throws GeneralSecurityException {
        SecureRandom sr = new SecureRandom();
        DESKeySpec dks = new DESKeySpec(key);
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(dks);
        Cipher cipher = Cipher.getInstance("DES/CBC/NoPadding");
        IvParameterSpec iv = new IvParameterSpec(icv);
        cipher.init(1, secretKey, iv, sr);
        return cipher.doFinal(content);
    }

    public static byte[] xOr(byte[] b1, byte[] b2) {
        byte[] tXor = new byte[Math.min(b1.length, b2.length)];
        for (int i = 0; i < tXor.length; i++)
            tXor[i] = (byte) (b1[i] ^ b2[i]);
        return tXor;
    }

    public static byte[] hexString2byte(String hexString) {
        if ((hexString == null) || (hexString.length() % 2 != 0) || (hexString.contains("null"))) {
            return null;
        }
        byte[] bytes = new byte[hexString.length() / 2];
        for (int i = 0; i < hexString.length(); i += 2) {
            bytes[(i / 2)] = (byte) (Integer.parseInt(hexString.substring(i, i + 2), 16) & 0xFF);
        }
        return bytes;
    }

    public static String byte2hexString(byte[] bytes) {
        StringBuffer buf = new StringBuffer(bytes.length * 2);
        for (int i = 0; i < bytes.length; i++) {
            if ((bytes[i] & 0xFF) < 16) {
                buf.append("0");
            }
            buf.append(Long.toString(bytes[i] & 0xFF, 16));
        }
        return buf.toString().toUpperCase();
    }




}