package org.modecp;

import com.google.common.hash.Hashing;
import com.google.common.io.BaseEncoding;
import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Security;

public class HashUtils {
    public static String RipeMd160(String hexString) throws NoSuchAlgorithmException {
        byte[] sha256Bytes = BaseEncoding.base16().decode(hexString.toUpperCase());
        Security.addProvider(new BouncyCastleProvider());
        MessageDigest md = MessageDigest.getInstance("RIPEMD160");
        byte[] ripemd160Bytes = md.digest(sha256Bytes);
        return BaseEncoding.base16().encode(ripemd160Bytes);
    }
    public static String Sha256(String hexPubKey) throws NoSuchAlgorithmException {
        byte[] bytes = BaseEncoding.base16().decode(hexPubKey.toUpperCase());
        String sha256Hex = Hashing.sha256().hashBytes(bytes).toString();
        return sha256Hex;
    }
    public static String toBase58(String hexString) throws NoSuchAlgorithmException {
        return "1"+Base58.encode(new BigInteger(hexString,16).toByteArray());
    }






}

