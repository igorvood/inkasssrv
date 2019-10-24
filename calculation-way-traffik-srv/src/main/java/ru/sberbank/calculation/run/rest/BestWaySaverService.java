package ru.sberbank.calculation.run.rest;

import ru.sberbank.inkass.dto.BestWayCandidateDto;
import ru.sberbank.inkass.dto.PointForSaveDto;

public interface BestWaySaverService {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    void savePoint(PointForSaveDto pointForSaveDto);

    void bestWayResult();
}
