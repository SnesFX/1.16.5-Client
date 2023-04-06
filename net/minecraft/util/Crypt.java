/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.util;

import java.security.Key;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.AlgorithmParameterSpec;
import java.security.spec.KeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import net.minecraft.util.CryptException;

public class Crypt {
    public static SecretKey generateSecretKey() throws CryptException {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
            keyGenerator.init(128);
            return keyGenerator.generateKey();
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }

    public static KeyPair generateKeyPair() throws CryptException {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
            keyPairGenerator.initialize(1024);
            return keyPairGenerator.generateKeyPair();
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }

    public static byte[] digestData(String string, PublicKey publicKey, SecretKey secretKey) throws CryptException {
        try {
            return Crypt.digestData(string.getBytes("ISO_8859_1"), secretKey.getEncoded(), publicKey.getEncoded());
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }

    private static byte[] digestData(byte[] ... arrby) throws Exception {
        MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
        for (byte[] arrby2 : arrby) {
            messageDigest.update(arrby2);
        }
        return messageDigest.digest();
    }

    public static PublicKey byteToPublicKey(byte[] arrby) throws CryptException {
        try {
            X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(arrby);
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePublic(x509EncodedKeySpec);
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }

    public static SecretKey decryptByteToSecretKey(PrivateKey privateKey, byte[] arrby) throws CryptException {
        byte[] arrby2 = Crypt.decryptUsingKey(privateKey, arrby);
        try {
            return new SecretKeySpec(arrby2, "AES");
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }

    public static byte[] encryptUsingKey(Key key, byte[] arrby) throws CryptException {
        return Crypt.cipherData(1, key, arrby);
    }

    public static byte[] decryptUsingKey(Key key, byte[] arrby) throws CryptException {
        return Crypt.cipherData(2, key, arrby);
    }

    private static byte[] cipherData(int n, Key key, byte[] arrby) throws CryptException {
        try {
            return Crypt.setupCipher(n, key.getAlgorithm(), key).doFinal(arrby);
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }

    private static Cipher setupCipher(int n, String string, Key key) throws Exception {
        Cipher cipher = Cipher.getInstance(string);
        cipher.init(n, key);
        return cipher;
    }

    public static Cipher getCipher(int n, Key key) throws CryptException {
        try {
            Cipher cipher = Cipher.getInstance("AES/CFB8/NoPadding");
            cipher.init(n, key, new IvParameterSpec(key.getEncoded()));
            return cipher;
        }
        catch (Exception exception) {
            throw new CryptException(exception);
        }
    }
}

