package com.qiming.eol_protocolapplayer;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class MyDataList {

    private List myData = Collections.synchronizedList(new LinkedList());

    public synchronized boolean add(String data) {
        return myData.add(data);
    }

    public synchronized String remove(int index) {
        if (myData.size() > 0) {
            return (String) myData.remove(index);
        } else {
            return null;
        }
    }

    public synchronized int size() {
        return myData.size();
    }

    public synchronized String get(int index) {
        if (myData.size() > 0) {
            return (String) myData.get(index);
        } else {
            return null;
        }
    }

    public synchronized void clear() {
        myData.clear();
    }

}
