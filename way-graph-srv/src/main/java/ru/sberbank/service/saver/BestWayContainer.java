package ru.sberbank.service.saver;

import ru.sberbank.inkass.dto.BestWayCandidateDto;

import java.util.List;

public interface BestWayContainer {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    List<BestWayCandidateDto> getBestWayCandidateDtos();
}
