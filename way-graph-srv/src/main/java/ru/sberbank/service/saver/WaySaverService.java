package ru.sberbank.service.saver;

import org.springframework.web.bind.annotation.GetMapping;
import ru.sberbank.inkass.dto.BestWayCandidateDto;

public interface WaySaverService {

    int saveBestWay(BestWayCandidateDto wayCandidate);

    BestWayCandidateDto getSuperWay();

    @GetMapping(value = "result/getSuperWay")
    BestWayCandidateDto getWorstWay();
}
