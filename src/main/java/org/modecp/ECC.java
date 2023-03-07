package org.modecp;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class ECC {
    public static final BigInteger Acurve = BigInteger.ZERO;
    public static final BigInteger Bcurve = BigInteger.valueOf(7);
    public static final BigInteger Pcurve = new BigInteger("115792089237316195423570985008687907853269984665640564039457584007908834671663");
    public static final BigInteger N = new BigInteger("115792089237316195423570985008687907852837564279074904382605163141518161494337");
    public static final BigInteger P = Pcurve;
    public static final BigInteger Gx = new BigInteger("55066263022277343669578718895168534326250603453777594175500187360389116729240");
    public static final BigInteger Gy = new BigInteger("32670510020758816978083085130507043184471273380659243275938904335757337482424");
    public static final BigInteger secp256k1_ndiv4 = new BigInteger("28948022309329048855892746252171976963317496166410141009864396001977208667915");
    public static final BigInteger secp256k1_ndiv4p1 = new BigInteger("28948022309329048855892746252171976963317496166410141009864396001977208667916");
    public static final BigInteger[] GPoint = {Gx, Gy};

    public static BigInteger modinv(BigInteger a, BigInteger n) { // Extended Euclidean Algorithm/'division' in elliptic curves
        BigInteger lm = BigInteger.ONE;
        BigInteger r = lm.mod(n);
        BigInteger hm = BigInteger.ZERO;
        BigInteger high = n;
        BigInteger low = a.mod(high);
        while (low.compareTo(BigInteger.ONE) > 0) {
            BigInteger ratio = high.divide(low);
            BigInteger nm = hm.subtract(lm.multiply(ratio));
            BigInteger news = high.subtract(low.multiply(ratio));
            hm = lm;
            lm = nm;
            high = low;
            low = news;
            r = lm.mod(n);
        }
        if (r.compareTo(BigInteger.ZERO) < 0) {
            r = r.add(n);
        }
        return r.mod(n);
    }
    public static BigInteger[] ECCadd(BigInteger[] a, BigInteger[] b) {
        BigInteger tmp = b[1].subtract(a[1]).mod(P);
        if (tmp.compareTo(BigInteger.ZERO) < 0) {
            tmp = tmp.add(P).mod(P);
        }
        BigInteger tmp2 = b[0].subtract(a[0]).mod(P);
        if (tmp2.compareTo(BigInteger.ZERO) < 0) {
            tmp2 = tmp2.add(P).mod(P);
        }
        BigInteger Lam = tmp.multiply(modinv(tmp2,P)).mod(P);
        BigInteger x = Lam.multiply(Lam).subtract(a[0]).subtract(b[0]).mod(P);
        if (x.compareTo(BigInteger.ZERO) < 0) {
            x = x.add(P).mod(P);
        }
        BigInteger y = Lam.multiply(a[0].subtract(x)).subtract(a[1]).mod(P);
        if (y.compareTo(BigInteger.ZERO) < 0) {
            y = y.add(P).mod(P);
        }
        return new BigInteger[] { x, y };
    }

    public static BigInteger[] InvertPoint(BigInteger[] point){
        return new BigInteger[] {point[0],Pcurve.subtract(point[1]).mod(Pcurve)};
    }
    public static BigInteger[] ECCsub(BigInteger[] a, BigInteger[] b){
        return ECCadd(a,InvertPoint(b));
    }
    public static BigInteger[] ECCdouble(BigInteger[] a) {

        BigInteger Lam = ((modinv(a[1].multiply(BigInteger.TWO), P)).multiply((a[0].multiply(a[0]).multiply(BigInteger.valueOf(3))))).mod(P);
        if (Lam.compareTo(BigInteger.ZERO) < 0) {
            Lam = Lam.add(P);
        }
        BigInteger x = (Lam.multiply(Lam).subtract(a[0].multiply(BigInteger.TWO))).mod(P);
        if (x.compareTo(BigInteger.ZERO) < 0) {
            x = x.add(P);
        }
        BigInteger y = (((a[0].subtract(x)).multiply(Lam)).subtract(a[1])).mod(P);
        if (y.compareTo(BigInteger.ZERO) < 0) {
            y = y.add(P);
        }
        return new BigInteger[] { x, y };
    }

    public static BigInteger[] ECCsplit(BigInteger[] point){
        return EccMultiply(point,modinv(BigInteger.TWO,N),point);
    }
    public static BigInteger[] EccMultiply(BigInteger[] GenPoint, BigInteger ScalarHex, BigInteger[] Startpoint) {
        String ScalarBin = ScalarHex.toString(2);
        BigInteger Qx = GenPoint[0];
        BigInteger Qy = GenPoint[1];
        for (int i = 1; i < ScalarBin.length(); i++) {
            BigInteger[] temp = ECCdouble(new BigInteger[]{Qx, Qy});
            Qx = temp[0];
            Qy = temp[1];
            if (ScalarBin.charAt(i) == '1') {
                temp = ECCadd(new BigInteger[]{Qx, Qy}, Startpoint);
                Qx = temp[0];
                Qy = temp[1];
            }
        }
        return new BigInteger[]{Qx, Qy};
    }
    public static BigInteger[] ECCdiv(BigInteger[] divpoint,BigInteger divkey){

        return EccMultiply(divpoint,modinv(divkey,N),divpoint);
    }
    public static BigInteger[] G(String BigIntString) {
        return EccMultiply(GPoint,new BigInteger(BigIntString),GPoint);
    }
    public static List<BigInteger[]> EccList(BigInteger[] GenPoint, BigInteger ScalarHex, BigInteger[] Startpoint) {
        List<BigInteger[]> returnList = new ArrayList<BigInteger[]>();
        BigInteger key=BigInteger.ONE;
        String ScalarBin = ScalarHex.toString(2);
        BigInteger Qx = GenPoint[0];
        BigInteger Qy = GenPoint[1];
        returnList.add(new BigInteger[]{Qx, Qy,key});
        for (int i = 1; i < ScalarBin.length(); i++) {
            BigInteger[] temp = ECCdouble(new BigInteger[]{Qx, Qy});
            Qx = temp[0];
            Qy = temp[1];
            key=key.multiply(BigInteger.TWO);
            returnList.add(new BigInteger[]{Qx, Qy,key});
            if (ScalarBin.charAt(i) == '1') {
                temp = ECCadd(new BigInteger[]{Qx, Qy}, Startpoint);
                Qx = temp[0];
                Qy = temp[1];
                key=key.add(BigInteger.ONE);
                returnList.add(new BigInteger[]{Qx, Qy,key});
            }

        }
        return returnList;
    }
    public static List<BigInteger[]> EccDivList(BigInteger[] GenPoint, BigInteger scalarHex, BigInteger[] Startpoint) {
        List<BigInteger[]> returnList = new ArrayList<>();
        String ScalarBin = new StringBuilder(scalarHex.toString(2)).reverse().toString();
        BigInteger key=scalarHex;
        BigInteger Qx = GenPoint[0];
        BigInteger Qy = GenPoint[1];
        returnList.add(new BigInteger[]{Qx, Qy,key});
        for (int i = 0; i < ScalarBin.length()-1; i++) {
            BigInteger[] temp;
            if (ScalarBin.charAt(i) == '1') {
                temp = ECCsub(new BigInteger[]{Qx, Qy}, Startpoint);
                Qx = temp[0];
                Qy = temp[1];
                key=key.subtract(BigInteger.ONE);
                returnList.add(new BigInteger[]{Qx, Qy,key});
            }
            temp = ECCsplit(new BigInteger[]{Qx, Qy});
            Qx = temp[0];
            Qy = temp[1];
            key=key.divide(BigInteger.TWO);
            returnList.add(new BigInteger[]{Qx, Qy,key});
        }
        return returnList;
    }

    public static Boolean ECCProof(BigInteger[] proofpoint) {
        return ((proofpoint[0]
                .multiply(proofpoint[0])
                .multiply(proofpoint[0]))
                .add(BigInteger.valueOf(7))
                .mod(Pcurve)
                .equals(
                        proofpoint[1]
                        .multiply(proofpoint[1])
                        .mod(Pcurve)
                        ));
        }
    public static BigInteger[] GetYFromX(BigInteger xpoint) {
        BigInteger x=xpoint.pow(3).add(BigInteger.valueOf(7));
        BigInteger possibleY1=x.modPow(secp256k1_ndiv4,Pcurve);
        BigInteger possibleY2=x.modPow(secp256k1_ndiv4p1,Pcurve);
        BigInteger possibleY3=Pcurve.subtract(possibleY1);
        BigInteger possibleY4=Pcurve.subtract(possibleY2);
        ArrayList <BigInteger> foundkeys= new ArrayList<>();
        if (ECCProof(new BigInteger[] {xpoint,possibleY1})==Boolean.TRUE){foundkeys.add(possibleY1);}
        if (ECCProof(new BigInteger[] {xpoint,possibleY2})==Boolean.TRUE){foundkeys.add(possibleY2);}
        if (ECCProof(new BigInteger[] {xpoint,possibleY3})==Boolean.TRUE){foundkeys.add(possibleY3);}
        if (ECCProof(new BigInteger[] {xpoint,possibleY4})==Boolean.TRUE){foundkeys.add(possibleY4);}
        BigInteger[] returnkeys=new BigInteger[foundkeys.size()];
        returnkeys=foundkeys.toArray(returnkeys);
        return returnkeys;
    }
    public static BigInteger[] GetXFromY(BigInteger ypoint) {
        BigInteger beta = new BigInteger("7ae96a2b657c07106e64479eac3434e99cf0497512f58995c1396c28719501ee",16);
        BigInteger xcubed=ypoint.pow(2).subtract(BigInteger.valueOf(7));
        BigInteger modpower=Pcurve.add(BigInteger.TWO).divide(BigInteger.valueOf(9));
        BigInteger X1=xcubed.modPow(modpower,Pcurve);
        BigInteger X2=X1.multiply(beta).mod(Pcurve);
        BigInteger X3=X1.multiply(beta).multiply(beta).mod(Pcurve);
        ArrayList <BigInteger> foundkeys= new ArrayList<>();
        if (ECCProof(new BigInteger[] {X1,ypoint})==Boolean.TRUE){foundkeys.add(X1);}
        if (ECCProof(new BigInteger[] {X2,ypoint})==Boolean.TRUE){foundkeys.add(X2);}
        if (ECCProof(new BigInteger[] {X3,ypoint})==Boolean.TRUE){foundkeys.add(X3);}
        BigInteger[] returnkeys=new BigInteger[foundkeys.size()];
        returnkeys=foundkeys.toArray(returnkeys);
        return returnkeys;
    }




}

