package org.modecp;

import com.google.common.hash.Hashing;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

public class CoinUtils {
    public static String Pubkey4(BigInteger[] point) {
        String pointx, pointy;
        if (point[0].toString(16).length() % 2 == 1)
            pointx = "0" + point[0].toString(16);
        else
            pointx = point[0].toString(16);
        if (point[1].toString(16).length() % 2 == 1)
            pointy = "0" + point[1].toString(16);
        else
            pointy = point[1].toString(16);
        //String pad="0000000000000000000000000000000000000000000000000000000000000000";
        String pad="0".repeat(64);
        String pointXpadded=pad.substring(pointx.length())+pointx;
        String pointYpadded=pad.substring(pointy.length())+pointy;
        return "04" + pointXpadded + pointYpadded;
    }
    public static String Pubkey3(BigInteger[] point) {
        String pad="0".repeat(64);
        return "03" + pad.substring(point[0].toString(16).length())+point[0].toString(16);
    }
    public static String Pubkey2(BigInteger[] point) {
        String pad="0".repeat(64);
        return "02" + pad.substring(point[0].toString(16).length())+point[0].toString(16);
    }
    public static String PointtoUncompressedPubkey(BigInteger[] point) {
        return Pubkey4(point).toUpperCase();
    }
    public static String PointtoCompressedPubkey(BigInteger[] point) {
        if (point[1].mod(BigInteger.TWO).equals(BigInteger.ZERO))
            return Pubkey2(point).toUpperCase();
        else
            return Pubkey3(point).toUpperCase();
    }
    public static String CompressedPubkeytoUncompressed(String compressedPubKEy) {
        String parity = compressedPubKEy.substring(0, 2);
        System.out.println(parity);
        BigInteger keyValue = new BigInteger(compressedPubKEy.substring(2), 16);
        BigInteger[] possibleY = ECC.GetYFromX(keyValue);
        BigInteger trueY;
        if (parity.equals("02")) {
            if (possibleY[0].mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
                trueY = possibleY[0];
            } else {
                trueY = possibleY[1];
            }
        } else {
            if (possibleY[0].mod(BigInteger.TWO).equals(BigInteger.ONE)) {
                trueY = possibleY[0];
            } else {
                trueY = possibleY[1];
            }
        }
        String UnCompressedPubKEy = PointtoUncompressedPubkey(new BigInteger[]{keyValue, trueY});
        return UnCompressedPubKEy;
    }
    public static String MakeCoinfromPrivateKeyUncompressed(BigInteger privateKey) throws NoSuchAlgorithmException {
        BigInteger[] GPoint = ECC.GPoint;
        BigInteger[] publickey= ECC.EccMultiply(GPoint,privateKey,GPoint);
        String hexPublicKey=CoinUtils.Pubkey4(publickey);
        String hexKeyHashed=HashUtils.Sha256(hexPublicKey);
        String ripemd160hexKeyHashed=HashUtils.hash160(hexKeyHashed);
        String netCodeKey="00"+ripemd160hexKeyHashed;
        String hash1forchecksum=HashUtils.Sha256(netCodeKey);
        String hash2forchecksum=HashUtils.Sha256(hash1forchecksum);
        String checksum= hash2forchecksum.substring(0,8);
        String addchecksum=netCodeKey+checksum;
        String BTCAddress=HashUtils.toBase58(addchecksum);
        return BTCAddress;
    }
    public static String MakeCoinfromPrivateKeyCompressed(BigInteger privateKey) throws NoSuchAlgorithmException {
        BigInteger[] GPoint = ECC.GPoint;
        BigInteger[] publickey= ECC.EccMultiply(GPoint,privateKey,GPoint);
        String hexPublicKey;
        if (publickey[1].mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            hexPublicKey = CoinUtils.Pubkey2(publickey);
        }else{
            hexPublicKey = CoinUtils.Pubkey3(publickey);
        }
        String hexKeyHashed=HashUtils.Sha256(hexPublicKey);
        String ripemd160hexKeyHashed=HashUtils.hash160(hexKeyHashed);
        String netCodeKey="00"+ripemd160hexKeyHashed;
        String hash1forchecksum=HashUtils.Sha256(netCodeKey);
        String hash2forchecksum=HashUtils.Sha256(hash1forchecksum);
        String checksum= hash2forchecksum.substring(0,8);
        String addchecksum=netCodeKey+checksum;
        String BTCAddress=HashUtils.toBase58(addchecksum);
        return BTCAddress;
    }
    public static String MakeCoinfromPointUncompressed(BigInteger[] point) throws NoSuchAlgorithmException {
        BigInteger[] publickey= point;
        String hexPublicKey=CoinUtils.Pubkey4(publickey);
        String hexKeyHashed=HashUtils.Sha256(hexPublicKey);
        String ripemd160hexKeyHashed=HashUtils.hash160(hexKeyHashed);
        String netCodeKey="00"+ripemd160hexKeyHashed;
        String hash1forchecksum=HashUtils.Sha256(netCodeKey);
        String hash2forchecksum=HashUtils.Sha256(hash1forchecksum);
        String checksum= hash2forchecksum.substring(0,8);
        String addchecksum=netCodeKey+checksum;
        String BTCAddress=HashUtils.toBase58(addchecksum);
        return BTCAddress;
    }
    public static String MakeCoinfromPointCompressed(BigInteger[] point) throws NoSuchAlgorithmException {
        BigInteger[] publickey= point;
        String hexPublicKey;
        if (publickey[1].mod(BigInteger.TWO).equals(BigInteger.ZERO)) {
            hexPublicKey = CoinUtils.Pubkey2(publickey);
        }else{
            hexPublicKey = CoinUtils.Pubkey3(publickey);
        }
        String hexKeyHashed=HashUtils.Sha256(hexPublicKey);
        String ripemd160hexKeyHashed=HashUtils.hash160(hexKeyHashed);
        String netCodeKey="00"+ripemd160hexKeyHashed;
        String hash1forchecksum=HashUtils.Sha256(netCodeKey);
        String hash2forchecksum=HashUtils.Sha256(hash1forchecksum);
        String checksum= hash2forchecksum.substring(0,8);
        String addchecksum=netCodeKey+checksum;
        String BTCAddress=HashUtils.toBase58(addchecksum);
        return BTCAddress;
    }
    public static BigInteger[] MakePointFromHexKey(String hexKey){
        String parity = hexKey.substring(0, 2);
        if (parity.equals("04")) {
            BigInteger xValue = new BigInteger(hexKey.substring(2, 66),16);
            BigInteger yValue = new BigInteger(hexKey.substring(66),16);
            return new BigInteger[]{xValue, yValue};
        }
        if (parity.equals("02")) {
            BigInteger xValue = new BigInteger(hexKey.substring(2),16);
            BigInteger[] possibleY= ECC.GetYFromX(xValue);
            BigInteger yValue;
            if (possibleY[0].mod(BigInteger.TWO).equals(BigInteger.ZERO)){
                yValue = possibleY[0];
            }else {
                yValue = possibleY[1];
            }
            return new BigInteger[]{xValue, yValue};
        }
        if (parity.equals("03")) {
            BigInteger xValue = new BigInteger(hexKey.substring(2),16);
            BigInteger[] possibleY= ECC.GetYFromX(xValue);
            BigInteger yValue;
            if (possibleY[0].mod(BigInteger.TWO).equals(BigInteger.ONE)){
                yValue = possibleY[0];
            }else {
                yValue = possibleY[1];
            }
            return new BigInteger[]{xValue, yValue};
        }

        return new BigInteger[]{BigInteger.ZERO,BigInteger.ZERO};

    }
    public static String MakeCoinFromHexKey(String hexKey) throws NoSuchAlgorithmException {
        String parity = hexKey.substring(0, 2);
        if (parity.equals("04")) {
            BigInteger[] point = MakePointFromHexKey(hexKey);
            return MakeCoinfromPointUncompressed(point);
        }else {
            BigInteger[] point = MakePointFromHexKey(hexKey);
            return MakeCoinfromPointCompressed(point);
        }
    }






    public static BigInteger RadomBigInt(){
        BigInteger maxLimit = ECC.N;
        BigInteger minLimit = BigInteger.ONE;
        BigInteger bigInteger = maxLimit.subtract(minLimit);
        Random randNum = new Random();
        int len = maxLimit.bitLength();
        BigInteger res = new BigInteger(len, randNum);
        if (res.compareTo(minLimit) < 0)
            res = res.add(minLimit);
        if (res.compareTo(bigInteger) >= 0)
            res = res.mod(bigInteger).add(minLimit);
        return res;
    }




}
