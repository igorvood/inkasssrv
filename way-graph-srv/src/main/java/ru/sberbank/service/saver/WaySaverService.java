package ru.sberbank.service.saver;

import ru.sberbank.inkass.dto.BestWayCandidateDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface WaySaverService {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    BestWayCandidateDto getSuperWay();

    BestWayCandidateDto getWorstWay();

    void saveBestWay(String algorithm, String pointName);

    ConcurrentHashMap<String, CopyOnWriteArrayList<String>> getBestWayResult();

}
