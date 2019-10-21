package ru.sberbank.service.saver;

import org.apache.commons.lang3.tuple.Pair;
import ru.sberbank.inkass.dto.AntTripTelemetryDto;
import ru.sberbank.inkass.dto.BestWayCandidateDto;
import ru.sberbank.inkass.dto.PointForSaveDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public interface WaySaverService {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    BestWayCandidateDto getSuperWay();

    BestWayCandidateDto getWorstWay();

    int savePoint(PointForSaveDto pointForSaveDto);

    ConcurrentHashMap<String, Pair<CopyOnWriteArrayList<String>, AntTripTelemetryDto>> getBestWayResult();

}
