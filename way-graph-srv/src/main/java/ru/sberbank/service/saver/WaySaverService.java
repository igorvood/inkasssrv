package ru.sberbank.service.saver;

import ru.sberbank.inkass.dto.BestWayCandidateDto;
import ru.sberbank.inkass.dto.PointForSaveDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface WaySaverService {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    BestWayCandidateDto getSuperWay();

    BestWayCandidateDto getWorstWay();

    int saveBestWay(PointForSaveDto pointForSaveDto);

    ConcurrentHashMap<String, CopyOnWriteArrayList<String>> getBestWayResult();

}
