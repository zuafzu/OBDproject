package com.cy.obdproject.tools;

public class ECU2Tools {


    public static byte[] _GetKey(byte[] seed) {
        //return _GetKey(0xA8CB7ADC, seed, 4);
        return _GetKeyAA(seed);
    }

    public static byte[] _GetKeyAA(byte[] seed) {
        long wort = 0;
        wort = (long) (seed[0] & 0xFF);
        wort = wort << 8;
        wort = wort | (long) (seed[1] & 0xFF);
        wort = wort << 8;
        wort = wort | (long) (seed[2] & 0xFF);
        wort = wort << 8;
        wort = wort | (long) (seed[3] & 0xFF);
        for (int i = 0; i < 35; i++) {
            if ((wort & 0x0000000080000000L) > 0) {
                wort = wort << 1;
                wort = wort ^ 0x00000000A8CB7ADCL;
            } else {
                wort = wort << 1;
            }
        }
        byte[] key = new byte[4];
        key[0] = (byte) (wort >> 24);
        key[1] = (byte) (wort >> 16);
        key[2] = (byte) (wort >> 8);
        key[3] = (byte) (wort >> 0);
        return key;
    }

    public static byte[] _GetKey(int mask, byte[] _seed, int seedLen) {
        int[] seed = new int[_seed.length];
        for (int i = 0; i < _seed.length; i++) {
            seed[i] = (int) ((_seed[i]) & 0xff);
        }

        int retLen = 0;
        byte[] bytes = new byte[4];
        byte[] key = new byte[4];
        long wort;
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

    public static byte[] _GetBootKey(Long iwSeed) {
        return _GetBootKey(0x6C5F40BB, iwSeed);
    }

    public static byte[] _GetBootKey(int iMASK, Long iwSeed) {
        long MASK = getUnsignedIntt(iMASK);
        long wSeed = getUnsignedIntt(iwSeed);
        long iterations;
        long wLastSeed;
        long wTemp;
        long wLSBit;
        long wTop31Bits;
        long jj, SB1, SB2, SB3;
        long temp;
        wLastSeed = wSeed;
        temp = getUnsignedIntt((int) ((((MASK & 0x00000800) >> 10) | ((MASK & 0x00200000) >> 21))));
        if (temp == 0) {
            wTemp = getUnsignedIntt((int) ((wSeed & 0xff000000) >> 24));
        } else if (temp == 1) {
            wTemp = getUnsignedIntt((int) ((wSeed & 0x00ff0000) >> 16));
        } else if (temp == 2) {
            wTemp = getUnsignedIntt((int) ((wSeed & 0x0000ff00) >> 8));
        } else {
            wTemp = getUnsignedIntt((int) (wSeed & 0x000000ff));
        }
        SB1 = getUnsignedIntt((int) ((MASK & 0x000003FC) >> 2));
        SB2 = getUnsignedIntt((int) (((MASK & 0x7F800000) >> 23) ^ 0xA5));
        SB3 = getUnsignedIntt((int) (((MASK & 0x001FE000) >> 13) ^ 0x5A));
        iterations = (long) (((wTemp ^ SB1) & SB2) + SB3);
        for (jj = 0; jj < iterations; jj++) {
            wTemp = ((wLastSeed & 0x40000000) / 0x40000000) ^ ((wLastSeed & 0x01000000) / 0x01000000) ^ ((wLastSeed & 0x1000) / 0x1000) ^ ((wLastSeed & 0x04) / 0x04);
            wLSBit = (wTemp & 0x00000001);
            wLastSeed = getUnsignedIntt((int) (wLastSeed << 1)); /* Left Shift the bits */
            wTop31Bits = getUnsignedIntt((int) (wLastSeed & 0xFFFFFFFE));
            wLastSeed = getUnsignedIntt((int) (wTop31Bits | wLSBit));
        }

        if ((MASK & 0x00000001) != 0) {
            wTop31Bits = ((wLastSeed & 0x00FF0000) >> 16) | ((wLastSeed & 0xFF000000) >> 8) | ((wLastSeed & 0x000000FF) << 8) | ((wLastSeed & 0x0000FF00) << 16);
        } else
            wTop31Bits = wLastSeed;

        wTop31Bits = (wTop31Bits ^ MASK);
        byte[] keys = new byte[4];
        keys[3] = (byte) wTop31Bits;
        keys[2] = (byte) (wTop31Bits >> 8);
        keys[1] = (byte) (wTop31Bits >> 16);
        keys[0] = (byte) (wTop31Bits >> 24);
        //String strResult = bytesToHexFun1(keys);
        return keys;
    }

    public static long CRC(short[] Buf) {
        return CRC(Buf, 0);
    }

    public static long CRC(short[] dataArray, int lastCRCvalue) {
        int[] icrc32tb = {0x00000000, 0x77073096, 0xEE0E612C, 0x990951BA, 0x076DC419, 0x706AF48F, 0xE963A535, 0x9E6495A3, 0x0EDB8832, 0x79DCB8A4, 0xE0D5E91E, 0x97D2D988, 0x09B64C2B, 0x7EB17CBD, 0xE7B82D07, 0x90BF1D91, 0x1DB71064, 0x6AB020F2, 0xF3B97148, 0x84BE41DE, 0x1ADAD47D, 0x6DDDE4EB, 0xF4D4B551, 0x83D385C7, 0x136C9856, 0x646BA8C0, 0xFD62F97A, 0x8A65C9EC, 0x14015C4F, 0x63066CD9, 0xFA0F3D63, 0x8D080DF5, 0x3B6E20C8, 0x4C69105E, 0xD56041E4, 0xA2677172, 0x3C03E4D1, 0x4B04D447, 0xD20D85FD, 0xA50AB56B, 0x35B5A8FA, 0x42B2986C, 0xDBBBC9D6, 0xACBCF940, 0x32D86CE3, 0x45DF5C75, 0xDCD60DCF, 0xABD13D59, 0x26D930AC, 0x51DE003A, 0xC8D75180, 0xBFD06116, 0x21B4F4B5, 0x56B3C423, 0xCFBA9599, 0xB8BDA50F, 0x2802B89E, 0x5F058808, 0xC60CD9B2, 0xB10BE924, 0x2F6F7C87, 0x58684C11, 0xC1611DAB, 0xB6662D3D, 0x76DC4190, 0x01DB7106, 0x98D220BC, 0xEFD5102A, 0x71B18589, 0x06B6B51F, 0x9FBFE4A5, 0xE8B8D433, 0x7807C9A2, 0x0F00F934, 0x9609A88E, 0xE10E9818, 0x7F6A0DBB, 0x086D3D2D, 0x91646C97, 0xE6635C01, 0x6B6B51F4, 0x1C6C6162, 0x856530D8, 0xF262004E, 0x6C0695ED, 0x1B01A57B, 0x8208F4C1, 0xF50FC457, 0x65B0D9C6, 0x12B7E950, 0x8BBEB8EA, 0xFCB9887C, 0x62DD1DDF, 0x15DA2D49, 0x8CD37CF3, 0xFBD44C65, 0x4DB26158, 0x3AB551CE, 0xA3BC0074, 0xD4BB30E2, 0x4ADFA541, 0x3DD895D7, 0xA4D1C46D, 0xD3D6F4FB, 0x4369E96A, 0x346ED9FC, 0xAD678846, 0xDA60B8D0, 0x44042D73, 0x33031DE5, 0xAA0A4C5F, 0xDD0D7CC9, 0x5005713C, 0x270241AA, 0xBE0B1010, 0xC90C2086, 0x5768B525, 0x206F85B3, 0xB966D409, 0xCE61E49F, 0x5EDEF90E, 0x29D9C998, 0xB0D09822, 0xC7D7A8B4, 0x59B33D17, 0x2EB40D81, 0xB7BD5C3B, 0xC0BA6CAD, 0xEDB88320, 0x9ABFB3B6, 0x03B6E20C, 0x74B1D29A, 0xEAD54739, 0x9DD277AF, 0x04DB2615, 0x73DC1683, 0xE3630B12, 0x94643B84, 0x0D6D6A3E, 0x7A6A5AA8, 0xE40ECF0B, 0x9309FF9D, 0x0A00AE27, 0x7D079EB1, 0xF00F9344, 0x8708A3D2, 0x1E01F268, 0x6906C2FE, 0xF762575D, 0x806567CB, 0x196C3671, 0x6E6B06E7, 0xFED41B76, 0x89D32BE0, 0x10DA7A5A, 0x67DD4ACC, 0xF9B9DF6F, 0x8EBEEFF9, 0x17B7BE43, 0x60B08ED5, 0xD6D6A3E8, 0xA1D1937E, 0x38D8C2C4, 0x4FDFF252, 0xD1BB67F1, 0xA6BC5767, 0x3FB506DD, 0x48B2364B, 0xD80D2BDA, 0xAF0A1B4C, 0x36034AF6, 0x41047A60, 0xDF60EFC3, 0xA867DF55, 0x316E8EEF, 0x4669BE79, 0xCB61B38C, 0xBC66831A, 0x256FD2A0, 0x5268E236, 0xCC0C7795, 0xBB0B4703, 0x220216B9, 0x5505262F, 0xC5BA3BBE, 0xB2BD0B28, 0x2BB45A92, 0x5CB36A04, 0xC2D7FFA7, 0xB5D0CF31, 0x2CD99E8B, 0x5BDEAE1D, 0x9B64C2B0, 0xEC63F226, 0x756AA39C, 0x026D930A, 0x9C0906A9, 0xEB0E363F, 0x72076785, 0x05005713, 0x95BF4A82, 0xE2B87A14, 0x7BB12BAE, 0x0CB61B38, 0x92D28E9B, 0xE5D5BE0D, 0x7CDCEFB7, 0x0BDBDF21, 0x86D3D2D4, 0xF1D4E242, 0x68DDB3F8, 0x1FDA836E, 0x81BE16CD, 0xF6B9265B, 0x6FB077E1, 0x18B74777, 0x88085AE6, 0xFF0F6A70, 0x66063BCA, 0x11010B5C, 0x8F659EFF, 0xF862AE69, 0x616BFFD3, 0x166CCF45, 0xA00AE278, 0xD70DD2EE, 0x4E048354, 0x3903B3C2, 0xA7672661, 0xD06016F7, 0x4969474D, 0x3E6E77DB, 0xAED16A4A, 0xD9D65ADC, 0x40DF0B66, 0x37D83BF0, 0xA9BCAE53, 0xDEBB9EC5, 0x47B2CF7F, 0x30B5FFE9, 0xBDBDF21C, 0xCABAC28A, 0x53B39330, 0x24B4A3A6, 0xBAD03605, 0xCDD70693, 0x54DE5729, 0x23D967BF, 0xB3667A2E, 0xC4614AB8, 0x5D681B02, 0x2A6F2B94, 0xB40BBE37, 0xC30C8EA1, 0x5A05DF1B, 0x2D02EF8D};
        long[] crc32tb = new long[256];
        for (int i = 0; i < 256; i++) {
            crc32tb[i] = getUnsignedIntt(icrc32tb[i]);
        }

        int oldcrc32;
        long crc32;
        long oldcrc;
        int charcnt;
        short c;
        int t;
        oldcrc32 = lastCRCvalue;
        charcnt = 0;

        for (int i = 0; i < dataArray.length; i++) {
            t = ((oldcrc32 >> 24) & 0xFF);
            c = dataArray[charcnt];
            oldcrc = crc32tb[t];
            oldcrc32 = ((oldcrc32 << 8) | c);
            oldcrc32 = (int) (oldcrc32 ^ oldcrc);
            charcnt++;
        }
        crc32 = getUnsignedIntt((int) oldcrc32);
        return crc32;
    }

    public static long getUnsignedIntt(Long data) {     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFFl;
    }

    public static long getUnsignedIntt(int data) {     //将int数据转换为0~4294967295 (0xFFFFFFFF即DWORD)。
        return data & 0x0FFFFFFFFl;
    }

    public static void VCIAlg(Boolean bIsEncript, int CMD, byte CRT, byte[] pInData, int iInDataSize, byte[] pOutData, int pOutDataSize) {

        int CMD_SET_CAN_PIN = 0x01;        //设置CAN线可配置引脚
        int CMD_SET_K_PIN = 0x02;        //设置K线可配置引脚
        int CMD_SET_L_PIN = 0x03;        //设置L线可配置引脚
        int CMD_SET_LIN_PIN = 0x04;        //设置LIN线可配置引脚
        int CMD_BEAT = 0x0A;        //心跳包
        int CMD_DISCONNECT_LINK = 0x0F;        //通知通信控制器断开逻辑链路

        int CMD_SET_K_SPEED = 0x10;        //设置K通信速率命令字
        int CMD_SET_K_TIMING_P1MIN = 0x11;        //设置K通信时间参数P1min
        int CMD_SET_K_TIMING_P1MAX = 0x12;        //设置K通信时间参数P1max
        int CMD_SET_K_TIMING_P4MIN = 0x13;        //设置K通信时间参数P4min
        int CMD_SET_K_TIMING_P4MAX = 0x14;        //设置K通信时间参数P4max
        int CMD_SET_K_WUP = 0x15;        //设置K线Wakeup时间
        int CMD_SET_K_IDLE = 0x16;        //设置K线总线空闲时间
        int CMD_SET_K_INIT = 0x17;        //设置K线低电平时间
        int CMD_K_START_COMM = 0x18;        //K线启动通信
        int CMD_K_TRANSMIT_DATA = 0x19;        //K线转发数据
        int CMD_K_PULL_UP_VOLTAGE = 0x1A;        //控制K线电压
        int CMD_SET_K_UART = 0x1B;        //配置奇偶校验、数据位、停止位
        int CMD_K_INIT_ADDRESS_TYPE = 0x1C;        //K线初始化地址方式
        int CMD_SET_K_TIMING_W1MAX = 0x1D;        //设置K线W1MAX时间
        int CMD_SET_K_TIMING_W1MIN = 0x1E;        //设置K线W1MIN时间
        int CMD_SET_K_TIMING_W2MAX = 0x1F;        //设置K线W2MAX时间
        int CMD_SET_K_TIMING_W2MIN = 0x20;        //设置K线W2MIN时间
        int CMD_SET_K_TIMING_W3MAX = 0x21;        //设置K线W3MAX时间
        int CMD_SET_K_TIMING_W3MIN = 0x22;        //设置K线W3MIN时间
        int CMD_SET_K_TIMING_W4MAX = 0x23;        //设置K线W4MAX时间
        int CMD_SET_K_TIMING_W4MIN = 0x24;        //设置K线W4MIN时间
        int CMD_SET_5BAUD_INIT_TYPE = 0x25;        //设置5波特率初始化类型
        int CMD_SET_5BAUD_INIT_PHYS_ADDRESS = 0x26;        //5波特率初始化物理地址寻址
        int CMD_SET_5BAUD_INIT_FUNC_ADDRESS = 0x27;        //5波特率初始化功能地址寻址
        int CMD_K_INIT_SETTINGS = 0x28;        //K线初始化设置
        int CMD_SET_TESTER_PRESENT_SEND_TYPE = 0x2A;        //诊断仪在线发送方式(0--周期发送, 1--空闲发送)
        int CMD_SET_TESTER_PRESENT_TIME = 0x2B;        //诊断仪在线发送最大时间间隔
        int CMD_TESTER_PRESENT_START_K = 0x2C;        //启动诊断仪在线K线功能
        int CMD_TESTER_PRESENT_STOP = 0x2D;        //停止诊断仪在线
        int CMD_TESTER_PRESENT_START_CAN_STD = 0x2F;        //启动诊断仪在线CAN线标准帧功能
        int CMD_TESTER_PRESENT_START_CAN_EXT = 0x30;        //启动诊断仪在线CAN线扩展帧功能
        int CMD_ECAS_COMM = 0x48;        //ECAS转发数据


        int CMD_SET_CAN_SPEED = 0x41;        //设置CAN通信速率命令字
        int CMD_SET_CAN_MASK = 0x42;        //设置CAN屏蔽字
        int CMD_CAN_TRANSMIT_STANDARD_FRAME = 0x44;        //CAN线转发标准帧
        int CMD_CAN_TRANSMIT_EXTEND_FRAME = 0x43;        //CAN线转发扩展帧

        int CMD_GET_SW_INFO = 0x50;        //获取软件信息
        int CMD_SET_HW_INFO = 0x51;        //设置硬件信息
        int CMD_GET_HW_INFO = 0x52;        //获取硬件信息

        int CMD_VCI_SAVE_BUFFER = 0x54;        //通知VCI硬件缓存数据
        int CMD_VCI_FLASH_SWITCH = 0x55;        //VCI硬件刷写开关
        int CMD_VCI_FLASH_CF_STMIN = 0x56;        //快速刷写时发送连续帧的时间间隔

        int CMD_GET_BATTERY_VOLTAGE = 0x63;        //获取电池电压
        int CMD_VCI_TESTERPRESENT_SWITCH = 0x64;        //VCI发送诊断仪在线功能

        int CMD_VCI_SET_15765_CANID = 0x70;        //ISO15765网络层设置CANID
        int CMD_VCI_SET_15765_StminInFC = 0x71;        //ISO15765网络层在流控中的Stmin时间
        int CMD_VCI_SET_15765_SF_INTERAL_TIME = 0x72;        //15765发送连续帧的时间间隔
        int CMD_VCI_SET_15765_BlockSizeInFC = 0x73;        //ISO15765网络层在流控中的BlockSize时间
        int CMD_VCI_SET_15765_SWITCH = 0x74;        //设置15765开关
        int CMD_VCI_SET_15765_SEND = 0x75;        //15765发送数据
        int CMD_VCI_SET_15765_RECV = 0x76;        //15765接收数据

        int CMD_VCI_SET_WLAN_OBD_PASSWORD = 0x82;        //设置无线OBD密码

        int CMD_VCI_START_RECORD_DATASTREAM = 0x85;        //启动VCI记录数据流

        int CMD_SET_CAN_MASK_BY_CANID = 0xA0;        //通过CANID设置屏蔽字


        if (CMD == CMD_SET_CAN_PIN || CMD == CMD_SET_CAN_SPEED || CMD == CMD_SET_CAN_MASK_BY_CANID) {
            //case CMD_SET_CAN_PIN:
            //case CMD_SET_CAN_SPEED:
            //case CMD_SET_CAN_MASK_BY_CANID:

            if (bIsEncript) {
                //奇数位一个算法，偶数位一个算法
                for (int i = 0; i < iInDataSize; i++) {
                    if (i % 2 == 0) {
                        pOutData[i] = (byte) ((pInData[i] ^ CRT) + CRT);
                    } else {
                        pOutData[i] = (byte) (~pInData[i] + CRT);
                    }
                }

                //首尾互换
                for (int i = 0; i < iInDataSize / 2; i++) {
                    byte ucTemp = pOutData[i];
                    pOutData[i] = pOutData[iInDataSize - i - 1];
                    pOutData[iInDataSize - i - 1] = ucTemp;
                }
            } else {
                for (int i = 0; i < iInDataSize / 2; i++) {
                    byte ucTemp = pInData[i];
                    pInData[i] = pInData[iInDataSize - i - 1];
                    pInData[iInDataSize - i - 1] = ucTemp;
                }

                for (int i = 0; i < iInDataSize; i++) {
                    if (i % 2 == 0) {
                        pOutData[i] = (byte) ((pInData[i] - CRT) ^ CRT);
                    } else {
                        pOutData[i] = (byte) ~(pInData[i] - CRT);
                    }
                }
            }

            pOutDataSize = iInDataSize;

        } else if (CMD == CMD_SET_K_PIN || CMD == CMD_SET_CAN_MASK || CMD == CMD_VCI_SET_15765_SWITCH) {
            //case CMD_SET_K_PIN:
            //case CMD_SET_CAN_MASK:
            //case CMD_VCI_SET_15765_SWITCH:

            if (bIsEncript) {
                //奇数位一个算法，偶数位一个算法
                for (int i = 0; i < iInDataSize; i++) {
                    if (i % 2 == 0) {
                        pOutData[i] = (byte) (~pInData[i] + CRT);
                    } else {
                        byte ucTemp = (byte) (((pInData[i] & 0x0F) << 4) | ((pInData[i] & 0xF0) >> 4));
                        pOutData[i] = (byte) (ucTemp ^ CRT);
                    }
                }

                //首尾互换
                for (int i = 0; i < iInDataSize / 2; i++) {
                    byte ucTemp = pOutData[i];
                    pOutData[i] = pOutData[iInDataSize - i - 1];
                    pOutData[iInDataSize - i - 1] = ucTemp;
                }
            } else {
                for (int i = 0; i < iInDataSize / 2; i++) {
                    byte ucTemp = pInData[i];
                    pInData[i] = pInData[iInDataSize - i - 1];
                    pInData[iInDataSize - i - 1] = ucTemp;
                }

                for (int i = 0; i < iInDataSize; i++) {
                    if (i % 2 == 0) {
                        pOutData[i] = (byte) ~(pInData[i] - CRT);
                    } else {
                        pInData[i] = (byte) (pInData[i] ^ CRT);
                        pOutData[i] = (byte) (((pInData[i] & 0x0F) << 4) | ((pInData[i] & 0xF0) >> 4));
                    }
                }
            }

            pOutDataSize = iInDataSize;

        } else if (CMD == CMD_SET_K_SPEED || CMD == CMD_VCI_FLASH_SWITCH)
        //	case CMD_SET_K_SPEED:
        //	case CMD_VCI_FLASH_SWITCH:
        {
            if (bIsEncript) {
                for (int i = 0; i < iInDataSize; i++) {
                    byte ucTemp = (byte) (((pInData[i] & 0x1F) << 3) | ((pInData[i] & 0xE0) >> 5));
                    pOutData[i] = (byte) ((ucTemp ^ CRT) + (char) i);
                }
            } else {
                for (int i = 0; i < iInDataSize; i++) {
                    pOutData[i] = (byte) ((pInData[i] - (char) i) ^ CRT);
                    pOutData[i] = (byte) (((pOutData[i] & 0xF8) >> 3) | ((pOutData[i] & 0x07) << 5));
                }
            }

            pOutDataSize = iInDataSize;
        } else {
            for (int i = 0; i < iInDataSize; i++) {
                pOutData[i] = pInData[i];
            }

            pOutDataSize = iInDataSize;
        }


    }

}
