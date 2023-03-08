package org.modecp;
//This is a modified version of Base58.java from bitcoinj,
//The original file can be downloaded from :
// https://github.com/bitcoinj/bitcoinj/blob/master/core/src/main/java/org/bitcoinj/base/Base58.java
import java.util.Arrays;
public class Base58 {
    public static final char[] ALPHABET = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz".toCharArray();

    public static String encode(byte[] input) {
        if (input.length == 0) {
            return "";
        } else {
            int zeros;
            for(zeros = 0; zeros < input.length && input[zeros] == 0; ++zeros) {
            }
            input = Arrays.copyOf(input, input.length);
            char[] encoded = new char[input.length * 2];
            int outputStart = encoded.length;
            int inputStart = zeros;

            while(inputStart < input.length) {
                --outputStart;
                encoded[outputStart] = ALPHABET[divmod(input, inputStart, 256, 58)];
                if (input[inputStart] == 0) {
                    ++inputStart;
                }
            }

            return new String(encoded, outputStart, encoded.length - outputStart);

        }
    }
    private static byte divmod(byte[] number, int firstDigit, int base, int divisor) {
        int remainder = 0;

        for(int i = firstDigit; i < number.length; ++i) {
            int digit = number[i] & 255;
            int temp = remainder * base + digit;
            number[i] = (byte)(temp / divisor);
            remainder = temp % divisor;
        }
        return (byte)remainder;
    }


}
