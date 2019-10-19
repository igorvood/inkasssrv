package ru.sberbank.service.saver;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface BestWayAccumulation {

    void reset();

    int saveBestWay(String algorithm, String pointName);

    ConcurrentHashMap<String, CopyOnWriteArrayList<String>> getResult();

}
