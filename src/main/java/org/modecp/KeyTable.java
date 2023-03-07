package org.modecp;

import java.io.*;
import java.math.BigInteger;
import java.util.HashMap;

public class KeyTable {


    public static Object saveStatus;
    public static Object loadStatus;

    public static void saveStatus(Serializable object,String fileName){
        try {
            FileOutputStream saveFile = new FileOutputStream(fileName);
            ObjectOutputStream out = new ObjectOutputStream(saveFile);
            out.writeObject(object);
            out.close();
            saveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static HashMap<Integer,BigInteger[]> loadStatus(String fileName){
        Object result = null;
        try {
            FileInputStream saveFile = new FileInputStream(fileName);
            ObjectInputStream in = new ObjectInputStream(saveFile);
            result = in.readObject();
            in.close();
            saveFile.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return (HashMap<Integer, BigInteger[]>) result;
    }
}
