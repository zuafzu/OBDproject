package com.cy.obdproject.base;

import com.github.mikephil.charting.data.LineDataSet;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

public class LineDataSetList {

    private List myData = Collections.synchronizedList(new LinkedList());

    public synchronized boolean add(LineDataSet data) {
        return myData.add(data);
    }

    public synchronized LineDataSet remove(int index) {
        if (myData.size() > 0) {
            return (LineDataSet) myData.remove(index);
        } else {
            return null;
        }
    }

    public synchronized int size() {
        return myData.size();
    }

    public synchronized LineDataSet get(int index) {
        if (myData.size() > 0) {
            return (LineDataSet) myData.get(index);
        } else {
            return null;
        }
    }

    public synchronized void clear() {
        myData.clear();
    }

}
