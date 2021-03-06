package com.qiming.eol_producefileparser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class InitClass {

    private byte[] allBytesData = null;// 刷写文件byte数组
    private byte[] bytesData = null;// 提供给36block的部分byte数组
    private List<Byte> crcBytesData = new ArrayList<>();
    private Map<String, EOLBean> map = new HashMap<>();
    private Map<String, WriteFileBean> eolMap = new HashMap<>();
    private int index = 0;

    private com.qiming.eol_public.InitClass publicUnit;

    public void setPublicUnit(com.qiming.eol_public.InitClass publicUnit) {
        this.publicUnit = publicUnit;
    }

    public void setAllBytesData(byte[] allBytesData) {
        this.allBytesData = allBytesData;
    }

    public byte[] getBytesData() {
        return bytesData;
    }

    public String HCUFileParserByteInit(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        index = 0;
        try {
            JSONObject jo = new JSONObject(inputdata);
//            String EOL_FILE_NAME = jo.optString("EOL_FILE_NAME");
            String DATA_AREAS = jo.optString("DATA_AREAS");
            // 根目录
//            File file = new File(Environment.getExternalStorageDirectory() + "/" + EOL_FILE_NAME);
//            if (file.exists()) {
//                byte[] buffer = FileUtil.readFile(file);
            if (allBytesData == null) {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：刷写文件字节为空");
                return jsonObject.toString();
            }
            byte[] buffer = allBytesData;
            if (!DATA_AREAS.equals("")) {
                map.clear();
                JSONArray jsonArray = new JSONArray(DATA_AREAS);
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject mJson = jsonArray.optJSONObject(i);
                    Iterator iterator = mJson.keys();
                    while (iterator.hasNext()) {
                        String key = iterator.next() + "";
                        EOLBean eolBean = new EOLBean();
                        eolBean.setStart(mJson.optString(key).split(",")[0]);
                        eolBean.setEnd(mJson.optString(key).split(",")[1]);
                        // eolBean.setLength();
                        eolBean.getDatas().clear();
                        map.put(key, eolBean);
                    }
                }
                byte[] mByte = {buffer[6]};
                int num = Integer.valueOf(StringTools.byte2hex(mByte), 16);
                int a = 0;
                for (int i = 0; i < num; i++) {
                    byte[] ba1 = new byte[4];
                    ba1[0] = buffer[i * 8 + 10 + a];
                    ba1[1] = buffer[i * 8 + 9 + a];
                    ba1[2] = buffer[i * 8 + 8 + a];
                    ba1[3] = buffer[i * 8 + 7 + a];
                    byte[] ba2 = new byte[4];
                    ba2[0] = buffer[i * 8 + 14 + a];
                    ba2[1] = buffer[i * 8 + 13 + a];
                    ba2[2] = buffer[i * 8 + 12 + a];
                    ba2[3] = buffer[i * 8 + 11 + a];
                    String str = StringTools.byte2hex(ba1);
                    String str2 = StringTools.byte2hex(ba2);
                    int b = (int) (Long.valueOf(str2, 16) - Long.valueOf(str, 16) + 1);
                    byte[] bytesData = ByteTools.subBytes(buffer, i * 8 + 14 + a + 1, b);

                    WriteFileBean writeFileBean = new WriteFileBean();
                    writeFileBean.setAddress(str);
                    writeFileBean.setEndAddress(str2);
                    writeFileBean.setLength(b + "");
                    writeFileBean.setData(bytesData);
                    for (Map.Entry<String, EOLBean> entry : map.entrySet()) {
                        String key = entry.getKey();
                        if (Long.valueOf(str, 16) >= Long.valueOf(map.get(key).getStart(), 16) &&
                                Long.valueOf(str2, 16) <= Long.valueOf(map.get(key).getEnd(), 16)) {
                            map.get(key).getDatas().add(writeFileBean);
                        }
                    }
                    a += b;
                }
                for (Map.Entry<String, EOLBean> entry : map.entrySet()) {
                    String key = entry.getKey();
                    byte[] bytes = new byte[0];
                    if (map.get(key).getDatas().size() > 1) {
                        for (int i = 0; i < map.get(key).getDatas().size(); i++) {
                            if (i == 0) {
                                bytes = map.get(key).getDatas().get(0).getData();
                            } else {
                                bytes = ByteTools.byteMerger(bytes, map.get(key).getDatas().get(i).getData());
                            }
                        }
                    } else {
                        bytes = map.get(key).getDatas().get(0).getData();
                    }
                    short[] aa = new short[bytes.length];
                    for (int j = 0; j < aa.length; j++) {
                        aa[j] = ByteTools.toShort(bytes[j]);
                    }
                    // String crc = Long.toHexString(CRC(aa));
                    String crc = StringTools.byte2hex(intToBytes(getCRC32(bytes)));
                    if (crc.length() == 1) {
                        crc = "0000000" + crc;
                    } else if (crc.length() == 2) {
                        crc = "000000" + crc;
                    } else if (crc.length() == 3) {
                        crc = "00000" + crc;
                    } else if (crc.length() == 4) {
                        crc = "0000" + crc;
                    } else if (crc.length() == 5) {
                        crc = "000" + crc;
                    } else if (crc.length() == 6) {
                        crc = "00" + crc;
                    } else if (crc.length() == 7) {
                        crc = "0" + crc;
                    }
                    map.get(key).setCrc(crc);
                }
                jsonObject.put("RESULT", "SUCCESS");
                jsonObject.put("DESC", "");
            } else {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "DATA_AREAS为空");
            }
//            } else {
//                jsonObject.put("RESULT", "FAULT");
//                jsonObject.put("DESC", "文件不存在");
//            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    public String HCUFileParserFileInit(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        index = 0;
        try {
            JSONObject jo = new JSONObject(inputdata);
            String EOL_FILE_NAME = jo.optString("EOL_FILE_NAME");
            String DATA_AREAS = jo.optString("DATA_AREAS");
            // 根目录
            File file = new File(com.qiming.eol_public.InitClass.pathShuaxie + "/" + EOL_FILE_NAME);
            if (file.exists()) {
                byte[] buffer = FileUtil.readFile(file);
                if (!DATA_AREAS.equals("")) {
                    map.clear();
                    JSONArray jsonArray = new JSONArray(DATA_AREAS);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject mJson = jsonArray.optJSONObject(i);
                        Iterator iterator = mJson.keys();
                        while (iterator.hasNext()) {
                            String key = iterator.next() + "";
                            EOLBean eolBean = new EOLBean();
                            eolBean.setStart(mJson.optString(key).split(",")[0]);
                            eolBean.setEnd(mJson.optString(key).split(",")[1]);
                            // eolBean.setLength();
                            eolBean.getDatas().clear();
                            map.put(key, eolBean);
                        }
                    }
                    byte[] mByte = {buffer[6]};
                    int num = Integer.valueOf(StringTools.byte2hex(mByte), 16);
                    int a = 0;
                    for (int i = 0; i < num; i++) {
                        byte[] ba1 = new byte[4];
                        ba1[0] = buffer[i * 8 + 10 + a];
                        ba1[1] = buffer[i * 8 + 9 + a];
                        ba1[2] = buffer[i * 8 + 8 + a];
                        ba1[3] = buffer[i * 8 + 7 + a];
                        byte[] ba2 = new byte[4];
                        ba2[0] = buffer[i * 8 + 14 + a];
                        ba2[1] = buffer[i * 8 + 13 + a];
                        ba2[2] = buffer[i * 8 + 12 + a];
                        ba2[3] = buffer[i * 8 + 11 + a];
                        String str = StringTools.byte2hex(ba1);
                        String str2 = StringTools.byte2hex(ba2);
                        int b = (int) (Long.valueOf(str2, 16) - Long.valueOf(str, 16) + 1);
                        byte[] bytesData = ByteTools.subBytes(buffer, i * 8 + 14 + a + 1, b);

                        WriteFileBean writeFileBean = new WriteFileBean();
                        writeFileBean.setAddress(str);
                        writeFileBean.setEndAddress(str2);
                        writeFileBean.setLength(b + "");
                        writeFileBean.setData(bytesData);
                        for (Map.Entry<String, EOLBean> entry : map.entrySet()) {
                            String key = entry.getKey();
                            if (Long.valueOf(str, 16) >= Long.valueOf(map.get(key).getStart(), 16) &&
                                    Long.valueOf(str2, 16) <= Long.valueOf(map.get(key).getEnd(), 16)) {
                                map.get(key).getDatas().add(writeFileBean);
                            }
                        }
                        a += b;
                    }
                    for (Map.Entry<String, EOLBean> entry : map.entrySet()) {
                        String key = entry.getKey();
                        byte[] bytes = new byte[0];
                        if (map.get(key).getDatas().size() > 1) {
                            for (int i = 0; i < map.get(key).getDatas().size(); i++) {
                                if (i == 0) {
                                    bytes = map.get(key).getDatas().get(0).getData();
                                } else {
                                    bytes = ByteTools.byteMerger(bytes, map.get(key).getDatas().get(i).getData());
                                }
                            }
                        } else {
                            bytes = map.get(key).getDatas().get(0).getData();
                        }
                        short[] aa = new short[bytes.length];
                        for (int j = 0; j < aa.length; j++) {
                            aa[j] = ByteTools.toShort(bytes[j]);
                        }
                        // String crc = Long.toHexString(CRC(aa));
                        String crc = StringTools.byte2hex(intToBytes(getCRC32(bytes)));
                        if (crc.length() == 1) {
                            crc = "0000000" + crc;
                        } else if (crc.length() == 2) {
                            crc = "000000" + crc;
                        } else if (crc.length() == 3) {
                            crc = "00000" + crc;
                        } else if (crc.length() == 4) {
                            crc = "0000" + crc;
                        } else if (crc.length() == 5) {
                            crc = "000" + crc;
                        } else if (crc.length() == 6) {
                            crc = "00" + crc;
                        } else if (crc.length() == 7) {
                            crc = "0" + crc;
                        }
                        map.get(key).setCrc(crc);
                    }
                    jsonObject.put("RESULT", "SUCCESS");
                    jsonObject.put("DESC", "");
                } else {
                    jsonObject.put("RESULT", "FAULT");
                    jsonObject.put("DESC", "DATA_AREAS为空");
                }
            } else {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "文件不存在");
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    public byte[] intToBytes(int value) {
        byte[] bytes = new byte[4];
        bytes[0] = (byte) (value >> 24);
        bytes[1] = (byte) (value >> 16);
        bytes[2] = (byte) (value >> 8);
        bytes[3] = (byte) (value >> 0);
        return bytes;
    }

    public int getCRC32(byte[] bytes) {
        int[] table = {
                0x00000000, 0x77073096, 0xee0e612c, 0x990951ba, 0x076dc419, 0x706af48f, 0xe963a535, 0x9e6495a3,
                0x0edb8832, 0x79dcb8a4, 0xe0d5e91e, 0x97d2d988, 0x09b64c2b, 0x7eb17cbd, 0xe7b82d07, 0x90bf1d91,
                0x1db71064, 0x6ab020f2, 0xf3b97148, 0x84be41de, 0x1adad47d, 0x6ddde4eb, 0xf4d4b551, 0x83d385c7,
                0x136c9856, 0x646ba8c0, 0xfd62f97a, 0x8a65c9ec, 0x14015c4f, 0x63066cd9, 0xfa0f3d63, 0x8d080df5,
                0x3b6e20c8, 0x4c69105e, 0xd56041e4, 0xa2677172, 0x3c03e4d1, 0x4b04d447, 0xd20d85fd, 0xa50ab56b,
                0x35b5a8fa, 0x42b2986c, 0xdbbbc9d6, 0xacbcf940, 0x32d86ce3, 0x45df5c75, 0xdcd60dcf, 0xabd13d59,
                0x26d930ac, 0x51de003a, 0xc8d75180, 0xbfd06116, 0x21b4f4b5, 0x56b3c423, 0xcfba9599, 0xb8bda50f,
                0x2802b89e, 0x5f058808, 0xc60cd9b2, 0xb10be924, 0x2f6f7c87, 0x58684c11, 0xc1611dab, 0xb6662d3d,
                0x76dc4190, 0x01db7106, 0x98d220bc, 0xefd5102a, 0x71b18589, 0x06b6b51f, 0x9fbfe4a5, 0xe8b8d433,
                0x7807c9a2, 0x0f00f934, 0x9609a88e, 0xe10e9818, 0x7f6a0dbb, 0x086d3d2d, 0x91646c97, 0xe6635c01,
                0x6b6b51f4, 0x1c6c6162, 0x856530d8, 0xf262004e, 0x6c0695ed, 0x1b01a57b, 0x8208f4c1, 0xf50fc457,
                0x65b0d9c6, 0x12b7e950, 0x8bbeb8ea, 0xfcb9887c, 0x62dd1ddf, 0x15da2d49, 0x8cd37cf3, 0xfbd44c65,
                0x4db26158, 0x3ab551ce, 0xa3bc0074, 0xd4bb30e2, 0x4adfa541, 0x3dd895d7, 0xa4d1c46d, 0xd3d6f4fb,
                0x4369e96a, 0x346ed9fc, 0xad678846, 0xda60b8d0, 0x44042d73, 0x33031de5, 0xaa0a4c5f, 0xdd0d7cc9,
                0x5005713c, 0x270241aa, 0xbe0b1010, 0xc90c2086, 0x5768b525, 0x206f85b3, 0xb966d409, 0xce61e49f,
                0x5edef90e, 0x29d9c998, 0xb0d09822, 0xc7d7a8b4, 0x59b33d17, 0x2eb40d81, 0xb7bd5c3b, 0xc0ba6cad,
                0xedb88320, 0x9abfb3b6, 0x03b6e20c, 0x74b1d29a, 0xead54739, 0x9dd277af, 0x04db2615, 0x73dc1683,
                0xe3630b12, 0x94643b84, 0x0d6d6a3e, 0x7a6a5aa8, 0xe40ecf0b, 0x9309ff9d, 0x0a00ae27, 0x7d079eb1,
                0xf00f9344, 0x8708a3d2, 0x1e01f268, 0x6906c2fe, 0xf762575d, 0x806567cb, 0x196c3671, 0x6e6b06e7,
                0xfed41b76, 0x89d32be0, 0x10da7a5a, 0x67dd4acc, 0xf9b9df6f, 0x8ebeeff9, 0x17b7be43, 0x60b08ed5,
                0xd6d6a3e8, 0xa1d1937e, 0x38d8c2c4, 0x4fdff252, 0xd1bb67f1, 0xa6bc5767, 0x3fb506dd, 0x48b2364b,
                0xd80d2bda, 0xaf0a1b4c, 0x36034af6, 0x41047a60, 0xdf60efc3, 0xa867df55, 0x316e8eef, 0x4669be79,
                0xcb61b38c, 0xbc66831a, 0x256fd2a0, 0x5268e236, 0xcc0c7795, 0xbb0b4703, 0x220216b9, 0x5505262f,
                0xc5ba3bbe, 0xb2bd0b28, 0x2bb45a92, 0x5cb36a04, 0xc2d7ffa7, 0xb5d0cf31, 0x2cd99e8b, 0x5bdeae1d,
                0x9b64c2b0, 0xec63f226, 0x756aa39c, 0x026d930a, 0x9c0906a9, 0xeb0e363f, 0x72076785, 0x05005713,
                0x95bf4a82, 0xe2b87a14, 0x7bb12bae, 0x0cb61b38, 0x92d28e9b, 0xe5d5be0d, 0x7cdcefb7, 0x0bdbdf21,
                0x86d3d2d4, 0xf1d4e242, 0x68ddb3f8, 0x1fda836e, 0x81be16cd, 0xf6b9265b, 0x6fb077e1, 0x18b74777,
                0x88085ae6, 0xff0f6a70, 0x66063bca, 0x11010b5c, 0x8f659eff, 0xf862ae69, 0x616bffd3, 0x166ccf45,
                0xa00ae278, 0xd70dd2ee, 0x4e048354, 0x3903b3c2, 0xa7672661, 0xd06016f7, 0x4969474d, 0x3e6e77db,
                0xaed16a4a, 0xd9d65adc, 0x40df0b66, 0x37d83bf0, 0xa9bcae53, 0xdebb9ec5, 0x47b2cf7f, 0x30b5ffe9,
                0xbdbdf21c, 0xcabac28a, 0x53b39330, 0x24b4a3a6, 0xbad03605, 0xcdd70693, 0x54de5729, 0x23d967bf,
                0xb3667a2e, 0xc4614ab8, 0x5d681b02, 0x2a6f2b94, 0xb40bbe37, 0xc30c8ea1, 0x5a05df1b, 0x2d02ef8d,
        };
        int crc = 0xffffffff;
        for (byte b : bytes) {
            crc = (crc >>> 8 ^ table[(crc ^ b) & 0xff]);
        }
        crc = crc ^ 0xffffffff;
        return crc;
    }

    public String HCUFileReadNext(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String AREA_NAME = jo.optString("AREA_NAME");
            EOLBean eolBean = map.get(AREA_NAME);
            if (eolBean.getDatas().size() > index) {
                jsonObject.put("BEGIN_ADDRESS", oderString(eolBean.getDatas().get(index).getAddress(), 8));
                String LEN = oderString(Integer.toHexString(Integer.valueOf(eolBean.getDatas().get(index).getLength())), 8);
                jsonObject.put("DATA_LEN_HEX", LEN);
                jsonObject.put("DATA_LEN", LEN);
                bytesData = eolBean.getDatas().get(index).getData();
                // jsonObject.put("DATA_MEMORY", StringTools.byte2hex(eolBean.getDatas().get(index).getData()));
                jsonObject.put("TOTAL_CHECK", eolBean.getCrc());

                jsonObject.put("RESULT", "SUCCESS");
                jsonObject.put("DESC", "");
                index++;
            } else {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "循环完毕");
                index = 0;
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    private String oderString(String string, int len) {
        if (string.length() > len) {
            return string.substring(string.length() - len, string.length());
        } else {
            int size = len - string.length();
            StringBuilder value = new StringBuilder();
            for (int i = 0; i < size; i++) {
                value.append("0");
            }
            return value.toString() + string;
        }
    }

    public String EOLFileParserByteInit(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        index = 0;
        try {
            if (allBytesData == null) {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：刷写文件字节为空");
                return jsonObject.toString();
            }
            byte[] buffer = allBytesData;
            eolMap.clear();
            byte[] mByte = {buffer[6]};
            int num = Integer.valueOf(StringTools.byte2hex(mByte), 16);
            int a = 0;
            for (int i = 0; i < num; i++) {
                byte[] ba1 = new byte[4];
                ba1[0] = buffer[i * 8 + 10 + a];
                ba1[1] = buffer[i * 8 + 9 + a];
                ba1[2] = buffer[i * 8 + 8 + a];
                ba1[3] = buffer[i * 8 + 7 + a];
                byte[] ba2 = new byte[4];
                ba2[0] = buffer[i * 8 + 14 + a];
                ba2[1] = buffer[i * 8 + 13 + a];
                ba2[2] = buffer[i * 8 + 12 + a];
                ba2[3] = buffer[i * 8 + 11 + a];
                String str = StringTools.byte2hex(ba1);
                String str2 = StringTools.byte2hex(ba2);
                int b = (int) (Long.valueOf(str2, 16) - Long.valueOf(str, 16) + 1);
                byte[] bytesData = ByteTools.subBytes(buffer, i * 8 + 14 + a + 1, b);

                WriteFileBean writeFileBean = new WriteFileBean();
                writeFileBean.setAddress(str);
                writeFileBean.setEndAddress(str2);
                writeFileBean.setLength(b + "");
                writeFileBean.setData(bytesData);
                String crc = StringTools.byte2hex(intToBytes(getCRC32(bytesData)));
                if (crc.length() == 1) {
                    crc = "0000000" + crc;
                } else if (crc.length() == 2) {
                    crc = "000000" + crc;
                } else if (crc.length() == 3) {
                    crc = "00000" + crc;
                } else if (crc.length() == 4) {
                    crc = "0000" + crc;
                } else if (crc.length() == 5) {
                    crc = "000" + crc;
                } else if (crc.length() == 6) {
                    crc = "00" + crc;
                } else if (crc.length() == 7) {
                    crc = "0" + crc;
                }
                writeFileBean.setCrc(crc);
                eolMap.put("" + i, writeFileBean);
                a += b;
            }
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    public String EOLFileReadNext(String inputdata) {
        //{"ADDRESS_LEN":"3","SIZE_LEN":"3","CHECK_LEN":"4"}
        JSONObject jsonObject = new JSONObject();
        try {
            JSONObject jo = new JSONObject(inputdata);
            String ADDRESS_LEN = jo.optString("ADDRESS_LEN").trim();
            String SIZE_LEN = jo.optString("SIZE_LEN").trim();
            String CHECK_LEN = jo.optString("CHECK_LEN").trim();
            if (eolMap.size() > index) {
                String key = String.valueOf(index);
                WriteFileBean writeFileBean = eolMap.get(key);
                String strBeginAddress = oderString(writeFileBean.getAddress(), 8);
                if (ADDRESS_LEN.equals("3")) {
                    strBeginAddress = strBeginAddress.substring(2, strBeginAddress.length());
                }
                jsonObject.put("BEGIN_ADDRESS", strBeginAddress);
                String LEN = oderString(Integer.toHexString(Integer.valueOf(writeFileBean.getLength())), 8);
                if (SIZE_LEN.equals("3")) {
                    LEN = LEN.substring(2, LEN.length());
                }
                jsonObject.put("DATA_LEN_HEX", LEN);
                jsonObject.put("DATA_LEN", LEN);
                bytesData = writeFileBean.getData();
                // jsonObject.put("DATA_MEMORY", StringTools.byte2hex(eolBean.getDatas().get(index).getData()));
                String crc = writeFileBean.getCrc();
                if (CHECK_LEN.equals("3")) {
                    crc = crc.substring(2, crc.length());
                }
                jsonObject.put("TOTAL_CHECK", crc);
                jsonObject.put("CHECKDATA", crc);
                jsonObject.put("RESULT", "SUCCESS");
                jsonObject.put("DESC", "");
                index++;
            } else {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "循环完毕");
                index = 0;
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    public String SetCRCData(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            for (int i = 0; i < bytesData.length; i++) {
                crcBytesData.add(bytesData[i]);
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    public String GetCRCValue(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");
            JSONObject jo = new JSONObject(inputdata);
            String CHECK_LEN = jo.optString("CHECK_LEN").trim();

            byte[] mmByte = new byte[crcBytesData.size()];
            for (int i = 0; i < crcBytesData.size(); i++) {
                mmByte[i] = crcBytesData.get(i);
            }
            String crc = StringTools.byte2hex(intToBytes(getCRC32(mmByte)));
            if (crc.length() == 1) {
                crc = "0000000" + crc;
            } else if (crc.length() == 2) {
                crc = "000000" + crc;
            } else if (crc.length() == 3) {
                crc = "00000" + crc;
            } else if (crc.length() == 4) {
                crc = "0000" + crc;
            } else if (crc.length() == 5) {
                crc = "000" + crc;
            } else if (crc.length() == 6) {
                crc = "00" + crc;
            } else if (crc.length() == 7) {
                crc = "0" + crc;
            }
            if (CHECK_LEN.equals("3")) {
                crc = crc.substring(2, crc.length());
            }
            jsonObject.put("DATA", crc);
            crcBytesData.clear();
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    /**
     * 多段数据计算校验值（只针对EOLFileParserByteInit）
     *
     * @param inputdata
     * @return
     */
    public String FileSemgentCheckData(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");

            JSONObject jo = new JSONObject(inputdata);
            String string = jo.optString("SMGTLIST");
            String[] strings = string.split(";");
            byte[] mmByte = null;
            // 解析每段校验的起始地址和长度，并封装需要校验的字节
            for (int i = 0; i < strings.length; i++) {
                String string1 = strings[i];
                String[] strings1 = string1.split("-");
                for (int j = 0; j < eolMap.size(); j++) {
                    if (Long.valueOf(strings1[0], 16).equals(Long.valueOf(eolMap.get("" + j).getAddress(), 16))) {
                        String mLen = strings1[1];
                        if (mLen != null && !"".equals(mLen.trim())) {
                            if (eolMap.get("" + j).getData().length >= Integer.valueOf(mLen, 16)) {
                                if (mmByte == null) {
                                    mmByte = ByteTools.subBytes(eolMap.get("" + j).getData(), 0, Integer.valueOf(mLen, 16));
                                } else {
                                    mmByte = ByteTools.byteMerger(mmByte,
                                            ByteTools.subBytes(eolMap.get("" + j).getData(), 0, Integer.valueOf(mLen, 16))
                                    );
                                }
                            } else {
                                jsonObject.put("RESULT", "FAULT");
                                jsonObject.put("DESC", "错误信息：校验长度有误");
                                return jsonObject.toString();
                            }
                        } else {
                            jsonObject.put("RESULT", "FAULT");
                            jsonObject.put("DESC", "错误信息：校验长度为空");
                            return jsonObject.toString();
                        }
                    }
                }

            }

            // 判断校验类型
            String CheckType = jo.optString("CheckType").trim();
            String OutLen = jo.optString("OutLen").trim();
            switch (CheckType) {
                case "CRC32":
                    // 计算CRC
                    String crc = StringTools.byte2hex(intToBytes(getCRC32(mmByte)));
                    if (crc.length() == 1) {
                        crc = "0000000" + crc;
                    } else if (crc.length() == 2) {
                        crc = "000000" + crc;
                    } else if (crc.length() == 3) {
                        crc = "00000" + crc;
                    } else if (crc.length() == 4) {
                        crc = "0000" + crc;
                    } else if (crc.length() == 5) {
                        crc = "000" + crc;
                    } else if (crc.length() == 6) {
                        crc = "00" + crc;
                    } else if (crc.length() == 7) {
                        crc = "0" + crc;
                    }
                    if (OutLen.equals("3")) {
                        crc = crc.substring(2, crc.length());
                    }
                    jsonObject.put("DATA", crc);
                    break;
                case "XOR":

                    break;
                case "SUM":

                    break;
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

    /**
     * 生产文件数据段排序（只针对EOLFileParserByteInit）
     *
     * @param inputdata
     * @return
     */
    public String FileSemgentSort(String inputdata) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("RESULT", "SUCCESS");
            jsonObject.put("DESC", "");

            JSONObject jo = new JSONObject(inputdata);
            // 判断排序类型ASC/DESC/CUSTOM/NONE
            String CheckType = jo.optString("SORTTYPE").trim();
            Map<String, WriteFileBean> mEolMap = new HashMap<>();
            switch (CheckType) {
                case "ASC":
                    // 升序
                    ArrayList<String> keys = new ArrayList<>();
                    for (int i = 0; i < eolMap.size(); i++) {
                        keys.add(eolMap.get("" + i).getAddress());
                    }
                    String temp = "";
                    int size = keys.size();
                    for (int i = 0; i < size - 1; i++) {
                        for (int j = 0; j < size - 1 - i; j++) {
                            if (Long.valueOf(keys.get(j), 16) > Long.valueOf(keys.get(j + 1), 16)) {
                                temp = keys.get(j);
                                keys.set(j, keys.get(j + 1));
                                keys.set(j + 1, temp);
                            }
                        }
                    }
                    for (int i = 0; i < keys.size(); i++) {
                        for (int j = 0; j < eolMap.size(); j++) {
                            if (keys.get(i).equals(eolMap.get("" + j).getAddress())) {
                                mEolMap.put("" + i, eolMap.get("" + j));
                            }
                        }
                    }
                    eolMap = mEolMap;
                    break;
                case "DESC":
                    // 降序
                    ArrayList<String> keys2 = new ArrayList<>();
                    for (int i = 0; i < eolMap.size(); i++) {
                        keys2.add(eolMap.get("" + i).getAddress());
                    }
                    String temp2 = "";
                    int size2 = keys2.size();
                    for (int i = 0; i < size2 - 1; i++) {
                        for (int j = 0; j < size2 - 1 - i; j++) {
                            if (Long.valueOf(keys2.get(j), 16) < Long.valueOf(keys2.get(j + 1), 16)) {
                                temp2 = keys2.get(j);
                                keys2.set(j, keys2.get(j + 1));
                                keys2.set(j + 1, temp2);
                            }
                        }
                    }
                    for (int i = 0; i < keys2.size(); i++) {
                        for (int j = 0; j < eolMap.size(); j++) {
                            if (keys2.get(i).equals(eolMap.get("" + j).getAddress())) {
                                mEolMap.put("" + i, eolMap.get("" + j));
                            }
                        }
                    }
                    eolMap = mEolMap;
                    break;
                case "CUSTOM":
                    // 自定义顺序
                    String string = jo.optString("SMGTLIST");
                    String[] strings = string.split(";");
                    //Map<String, String> map = new HashMap<>();// 每段的起始地址和长度
                    // 解析每段的起始地址和长度
                    for (int i = 0; i < strings.length; i++) {
                        String string1 = strings[i];
                        String[] strings1 = string1.split("-");

                        String BeginAddr = strings1[0];
                        String Len = strings1[1];
                        //map.put(BeginAddr, Len);

                        for (int j = 0; j < eolMap.size(); j++) {
                            WriteFileBean writeFileBean = eolMap.get("" + j);
                            if (Long.valueOf(writeFileBean.getAddress(), 16).equals(Long.valueOf(BeginAddr, 16))) {
                                byte[] bytes = writeFileBean.getData();
                                if (Integer.valueOf(Len, 16) <= bytes.length) {
                                    byte[] mBytes = new byte[Integer.valueOf(Len, 16)];
                                    for (int k = 0; k < mBytes.length; k++) {
                                        mBytes[k] = bytes[k];
                                    }
                                    writeFileBean.setData(mBytes);
                                    mEolMap.put(i + "", writeFileBean);
                                } else {
                                    jsonObject.put("RESULT", "FAULT");
                                    jsonObject.put("DESC", "错误信息：校验长度有误");
                                    return jsonObject.toString();
                                }
                            }
                        }
                    }
                    eolMap = mEolMap;
                    break;
                case "NONE":
                    // 不排序

                    break;
            }
        } catch (Exception e) {
            LogTools.errLog(e);
            try {
                jsonObject.put("RESULT", "FAULT");
                jsonObject.put("DESC", "错误信息：" + e.getMessage());
            } catch (JSONException e1) {
                LogTools.errLog(e1);
            }
        }
        return jsonObject.toString();
    }

}
