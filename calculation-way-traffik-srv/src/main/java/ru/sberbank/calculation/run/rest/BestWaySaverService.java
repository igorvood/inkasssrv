package ru.sberbank.calculation.run.rest;

import ru.sberbank.inkass.dto.BestWayCandidateDto;

public interface BestWaySaverService {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    void savePoint(String algorithm, String savePoint);

    void bestWayResult();
}
