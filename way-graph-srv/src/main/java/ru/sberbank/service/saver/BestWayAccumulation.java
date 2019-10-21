package ru.sberbank.service.saver;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.AntTripTelemetryDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface BestWayAccumulation {

    void reset();

    int saveBestWay(String algorithm, String pointName);

    ConcurrentHashMap<String, Pair<CopyOnWriteArrayList<String>, AntTripTelemetryDto>> getResult();

}
