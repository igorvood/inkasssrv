package ru.sberbank.service.saver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.inkass.dto.BestWayCandidateDto;

import java.util.Comparator;
import java.util.logging.Logger;


@RestController
public class WaySaverServiceImpl implements WaySaverService {

    private static final Logger logger = Logger.getLogger(WaySaverServiceImpl.class.getName());

    private final BestWayContainer wayContainer;

    public WaySaverServiceImpl(BestWayContainer wayContainer) {
        this.wayContainer = wayContainer;
    }

    @Override
    @PostMapping(value = "result/saveBestWayCandidate")
    public int saveBestWay(@RequestBody BestWayCandidateDto wayCandidate) {
        logger.info(String.format("Save best way %s", wayCandidate.getTotalMoney()));
        return wayContainer.saveBestWay(wayCandidate);
    }

    @Override
    @GetMapping(value = "result/getSuperWay")
    public BestWayCandidateDto getSuperWay() {
        return wayContainer.getBestWayCandidateDtos().stream()
                .max(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
                .orElse(null);
    }

    @Override
    @GetMapping(value = "result/getWorstWay")
    public BestWayCandidateDto getWorstWay() {
        return wayContainer.getBestWayCandidateDtos().stream()
                .min(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
                .orElse(null);
    }


}
