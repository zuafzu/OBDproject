package com.cy.obdproject.tools;

public class ECU2Tools {


    public static byte[] _GetKey(byte[] seed) {
        return _GetKey(0x4a68795b, seed, 4);
    }

    public static byte[] _GetKey(int mask, byte[] seed, int seedLen) {

        int retLen = 0;
        byte[] bytes = new byte[4];
        byte[] key = new byte[4];
        int wort;
        if (seed[1] == 0 && seed[2] == 0)
            return null;
        else {
            retLen = seedLen - 1;
            wort = seed[0] << 24 | seed[1] << 16 | seed[2] << 8 | seed[3];
            for (int i = 0; i < 35; i++) {
                if ((wort & 0x80000000) != 0) {
                    wort = (wort << 1);
                    wort = (wort ^ mask);
                } else {
                    wort = (wort << 1);
                }
            }
            bytes[0] = (byte) wort;
            bytes[1] = (byte) (wort >> 8);
            bytes[2] = (byte) (wort >> 16);
            bytes[3] = (byte) (wort >> 24);
            for (int i = 0; i < 4; i++) {
                key[3 - i] = bytes[i];
            }
        }
        return key;
    }

    public static byte[] getBootKey(byte[] seed) {
        return getBootKey(new byte[]{0x4a,0x68,0x79,0x5b}, seed);
    }

    public static byte[] getBootKey(byte[] mask, byte[] seed) {
        //original call parameter is both int32.
        int MASK = mask[0] << 24 | mask[1] << 16 | mask[2] << 8 | mask[3];
        int wSeed = seed[0] << 24 | seed[1] << 16 | seed[2] << 8 | seed[3];
        int iterations;
        int wLastSeed;
        int wTemp;
        int wLSBit;
        int wTop31Bits;
        int jj, SB1, SB2, SB3;
        int temp;
        wLastSeed = wSeed;
        temp = (int) ((MASK & 0x00000800) >>> 10) | ((MASK & 0x00200000) >>> 21);
        if (temp == 0) {
            wTemp = (int) ((wSeed & 0xff000000) >>> 24);
        } else if (temp == 1) {
            wTemp = (int) ((wSeed & 0x00ff0000) >>> 16);
        } else if (temp == 2) {
            wTemp = (int) ((wSeed & 0x0000ff00) >>> 8);
        } else {
            wTemp = (int) (wSeed & 0x000000ff);
        }
        SB1 = (int) ((MASK & 0x000003FC) >>> 2);
        SB2 = (int) (((MASK & 0x7F800000) >>> 23) ^ 0xA5);
        SB3 = (int) (((MASK & 0x001FE000) >>> 13) ^ 0x5A);
        iterations = (int) (((wTemp ^ SB1) & SB2) + SB3);
        for (jj = 0; jj < iterations; jj++) {
            wTemp = ((wLastSeed & 0x40000000) / 0x40000000) ^ ((wLastSeed & 0x01000000) / 0x01000000)
                    ^ ((wLastSeed & 0x1000) / 0x1000) ^ ((wLastSeed & 0x04) / 0x04);
            wLSBit = (wTemp & 0x00000001);
            wLastSeed = (int) (wLastSeed << 1); /* Left Shift the bits */
            wTop31Bits = (int) (wLastSeed & 0xFFFFFFFE);
            wLastSeed = (int) (wTop31Bits | wLSBit);
        }
        if ((MASK & 0x00000001) != 0) {
            wTop31Bits = ((wLastSeed & 0x00FF0000) >>> 16) | ((wLastSeed & 0xFF000000) >>> 8)
                    | ((wLastSeed & 0x000000FF) << 8) | ((wLastSeed & 0x0000FF00) << 16);
        } else
            wTop31Bits = wLastSeed;
        wTop31Bits = wTop31Bits ^ MASK;
        //original return value is int32(wTop31Bits).
        byte[] res = new byte[4];
        res[0] = (byte) wTop31Bits;
        res[1] = (byte) (wTop31Bits >> 8);
        res[2] = (byte) (wTop31Bits >> 16);
        res[3] = (byte) (wTop31Bits >> 24);
        return res;
        //return (wTop31Bits);
    }

    public static long CalculationCheck(byte[] Buf) {
        int CrcVak= 0xFFFFFFFF;
        int islst = 1;
        return CalculationCheck(Buf,CrcVak,islst);
    }

    public static long CalculationCheck(byte[] Buf,int CrcVak ,int islast) {

        int[] pBuf = new int[Buf.length];
        for(int i = 0; i < pBuf.length; i ++) {
            pBuf[i] = getUnsignedByte(Buf[i]);
        }

        long mask = getUnsignedIntt(0xEDB88320);
        long[] tabelle = new long[256];
        long Crcval = getUnsignedIntt(CrcVak);

        for(int num = 0; num < 256; num ++) {
            long  crc = getUnsignedIntt(num);

            for(int i = 0;i < 8; i ++) {
                if((crc & 0x00000001) == 1) {
                    crc = ((crc >> 1) ^ mask);
                }else {
                    if(crc > 0) {
                        crc = (crc >> 1);
                    }
                }
            }
            tabelle[num] = crc;
        }

        for(int i = 0; i < pBuf.length; i ++) {
            int tmp = (getUnsignedByte((byte)Crcval) ^ pBuf[i]) ;
            Crcval = ((Crcval >> 8) ^ tabelle[tmp]);
        }

        if(islast == 1) {
            Crcval = (Crcval ^ 0xFFFFFFFF);
        }

        return Crcval;
    }

    public static int getUnsignedByte (byte data){      //将data字节型数据转换为0~255 (0xFF 即BYTE)。
        return data&0x0FF;
    }

    public static int getUnsignedByte (short data){      //将data字节型数据转换为0~65535 (0xFFFF 即 WORD)。
        return data&0x0FFFF;
    }

    public static long getUnsignedIntt (int data){     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data&0x0FFFFFFFFl;
    }

}
