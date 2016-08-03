package com.wimds.wimds.Util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Created by han on 2016-05-31.
 */
public class ParseClass {
    /*
    GPS에서 String형태로 받아온 데이터를 double 형으로 변환
    매개변수 : string
    return 타입 : double
     */
    public static double hexToByteArray(String hex) {
        double d = 0.0;
        if (hex == null || hex.length() == 0) {
            return 0;
        }

        byte[] ba = new byte[hex.length() / 2];
        for (int i = 0; i < ba.length; i++) {
            ba[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        d = ByteBuffer.wrap(ba).order(ByteOrder.LITTLE_ENDIAN).getDouble();
        return d;
    }

    public static double GPStoWGS(double dNMEA){
        int  iDegree1, iDegree2;
        double dSec, dMin, dWGS84;
        dNMEA = dNMEA / 100;
        iDegree1 = (int)dNMEA;        //도
        iDegree2 = (int)(dNMEA * 100);
        dSec  = (dNMEA * 100 - iDegree2) * 60;   //초, 60단위로 (원래 100단위임)
        dMin  = (iDegree2 - (iDegree1 * 100));   //분, 60단위로 (원래 60단위임)
        dWGS84  = iDegree1 + (dMin / 60) + (dSec / 3600); //도 단위로 분, 초
        return dWGS84;
    }
}
