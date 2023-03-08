//Mode Crypto Platform
package org.modecp;

import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Main {
    public static void main(String[] args) throws NoSuchAlgorithmException {

/*
        //BigInteger privatekey=ECC.N.subtract(BigInteger.ONE);
        //BigInteger privatekey=BigInteger.ONE;
        //BigInteger privatekey=ECC.N.divide(BigInteger.TWO).add(BigInteger.ONE);

        BigInteger privatekey=ECC.N.divide(BigInteger.TWO);

        BigInteger[] keypair=ECC.EccMultiply(ECC.GPoint,privatekey,ECC.GPoint);
        String compressedkey=CoinUtils.PointtoCompressedPubkey(keypair);
        System.out.println("compressed: "+compressedkey);
        String uncommpressedkey=CoinUtils.PointtoUncompressedPubkey(keypair);
        System.out.println(keypair[0]+","+keypair[1]);
        System.out.println(privatekey.toString(16));
        System.out.println(uncommpressedkey);
        System.out.println(CoinUtils.MakeCoinfromPointUncompressed(keypair));
        System.out.println(CoinUtils.MakePointFromHexKey(uncommpressedkey)[0]+","+CoinUtils.MakePointFromHexKey(uncommpressedkey)[1]);
        System.out.println(CoinUtils.MakePointFromHexKey(compressedkey)[0]+","+CoinUtils.MakePointFromHexKey(compressedkey)[1]);
        System.out.println(uncommpressedkey+":"+CoinUtils.MakeCoinFromHexKey(uncommpressedkey));
        System.out.println(compressedkey+":"+CoinUtils.MakeCoinFromHexKey(compressedkey));

        BigInteger[] threekey=ECC.EccMultiply(ECC.GPoint,BigInteger.valueOf(3),ECC.GPoint);
        BigInteger[] twokey=ECC.EccMultiply(ECC.GPoint,BigInteger.TWO,ECC.GPoint);
        BigInteger[] fourkey=ECC.EccMultiply(ECC.GPoint,BigInteger.valueOf(4),ECC.GPoint);
        BigInteger[] eightkey=ECC.EccMultiply(ECC.GPoint,BigInteger.valueOf(8),ECC.GPoint);

        System.out.println(twokey[0]+","+twokey[1]);
        System.out.println(ECC.InvertPoint(twokey)[0]+","+ECC.InvertPoint(twokey)[1]);
        System.out.print(ECC.ECCsub(ECC.GPoint,ECC.GPoint)[0]);
        System.out.print(",");
        System.out.println(ECC.ECCsub(ECC.GPoint,ECC.GPoint)[1]);
        System.out.print(fourkey[0]);
        System.out.print(",");
        System.out.println(fourkey[1]);
        System.out.print(ECC.ECCdiv(eightkey,BigInteger.TWO,eightkey)[0]);
        System.out.print(",");
        System.out.println(ECC.ECCdiv(eightkey,BigInteger.TWO,eightkey)[1]);*/

        List<BigInteger[]> example = ECC.EccList(ECC.GPoint, BigInteger.TEN, ECC.GPoint);
        example.forEach(x -> System.out.println(x[0] + " " + x[2]));
        System.out.println(" ---");
        List<BigInteger[]> example2 = ECC.EccDivList(ECC.G("1000"), new BigInteger("1000"), ECC.GPoint);
        example2.forEach(x -> System.out.println(x[0] + " " + x[2]));
        System.out.println("");


        //Keytable needs to store a <Integer,Coin> where Coin is a class object of the public key including private key
        HashMap<Integer, BigInteger[]> keyTable = new HashMap<>();
        keyTable.put(5506, new BigInteger[]{ECC.GPoint[0], ECC.GPoint[1], new BigInteger("1")});
        keyTable.put(8956, new BigInteger[]{ECC.G("2")[0], ECC.G("2")[1], new BigInteger("2")});

        KeyTable.saveStatus(keyTable, "KeyTable.dat");
        HashMap<Integer, BigInteger[]> keyTableL = new HashMap<>();
        keyTableL = KeyTable.loadStatus("KeyTable.dat");

        //simple isinKeytable

        if (keyTable.get(5506)[0] == ECC.G("1")[0]) {
            System.out.println(keyTable.get(5506)[2]);
        } else {
            System.out.println(Boolean.FALSE);
        }

        System.out.println(CoinUtils.MakeCoinfromPrivateKeyUncompressed(BigInteger.TEN));
        System.out.println(CoinUtils.MakeCoinfromPrivateKeyUncompressed(ECC.N.divide(BigInteger.TWO)));
        System.out.println(CoinUtils.MakeCoinfromPrivateKeyUncompressed(ECC.N.divide(BigInteger.TEN)));
        System.out.println(CoinUtils.PrivateKeyToHexPrivateKey(ECC.N.divide(BigInteger.TEN)));


        String name = "YooPhGluP";
        StringBuilder test = new StringBuilder("iiii");
        Boolean found=Boolean.TRUE;
        BigInteger iterate=CoinUtils.RadomBigInt();
        BigInteger[] pubkey= ECC.G(iterate.toString());
        while (found==Boolean.TRUE) {
            iterate=iterate.add(BigInteger.ONE);
            pubkey=ECC.ECCadd(pubkey,ECC.GPoint);
            String coin=CoinUtils.MakeCoinfromPointUncompressed(pubkey);
            //System.out.print(".");
            if (coin.toString().toUpperCase().contains(name.toUpperCase())){
                //found=Boolean.FALSE;
                System.out.println("Key: "+iterate);
                System.out.println(coin);
            }
            String coin2=CoinUtils.MakeCoinfromPointCompressed(pubkey);
            //System.out.print(".");
            if (coin2.toString().toUpperCase().contains(name.toUpperCase())){
                //found=Boolean.FALSE;
                System.out.println("Key: "+iterate);
                System.out.println(coin2);
            }
            BigInteger[] ipubkey={pubkey[0],ECC.N.subtract(pubkey[1])};
            String coin3=CoinUtils.MakeCoinfromPointCompressed(ipubkey);
            //System.out.print(".");
            if (coin3.toString().toUpperCase().contains(name.toUpperCase())){
                //found=Boolean.FALSE;
                System.out.println("Key: "+iterate);
                System.out.println(coin3);
            }

        }

    }



}
