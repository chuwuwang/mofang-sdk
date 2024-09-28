package com.morefun.ysdk.sample.utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class Cipher {
    protected Algorithm algorithm;
    protected Mode mode;
    protected Padding padding;
    protected byte[] key;
    protected byte[] data;
    protected byte[] iv;

    public Cipher setAlgorithm(Algorithm algorithm) {
        this.algorithm = algorithm;
        return this;
    }

    public Cipher setMode(Mode mode) {
        this.mode = mode;
        return this;
    }

    public Cipher setPadding(Padding padding) {
        this.padding = padding;
        return this;
    }

    public Cipher setKey(byte[] key) {
        this.key = key;
        return this;
    }

    public Cipher setData(byte[] data) {
        this.data = data;
        return this;
    }

    public Cipher setIv(byte[] iv) {
        this.iv = iv;
        return this;
    }

    public byte[] encrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return execute(javax.crypto.Cipher.ENCRYPT_MODE).doFinal(data);
    }

    public byte[] deCrypt() throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        return execute(javax.crypto.Cipher.DECRYPT_MODE).doFinal(data);
    }

    private javax.crypto.Cipher execute(int cryptMode) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidAlgorithmParameterException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        SecretKeySpec sKey = new SecretKeySpec(key, algorithm.nameAlg);
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance(
                algorithm.nameAlg
                        + "/" + mode.name()
                        + "/" + padding.name()
                , new BouncyCastleProvider());

        cipher.init(cryptMode, sKey, mode == Mode.ECB ? null : new IvParameterSpec(iv == null ? new byte[8] : iv));//ecb禁止IvParameterSpec参数
        return cipher;
    }

    public enum Algorithm {
        DES("DES"),
        DES_Triple("DESede");
        String nameAlg;

        Algorithm(String name) {
            this.nameAlg = name;
        }
    }

    public enum Mode {
        ECB,
        CBC;
    }

    public enum Padding {
        PKCS5Padding,
        PKCS7Padding,
        NoPadding
    }

}



