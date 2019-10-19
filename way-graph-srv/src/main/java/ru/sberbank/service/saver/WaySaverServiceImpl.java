package ru.sberbank.service.saver;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.inkass.dto.BestWayCandidateDto;
import ru.sberbank.inkass.dto.PointForSaveDto;

import java.util.Comparator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;


@RestController
public class WaySaverServiceImpl implements WaySaverService {

    private static final Logger logger = Logger.getLogger(WaySaverServiceImpl.class.getName());

    private final BestWayContainer wayContainer;

    private final BestWayAccumulation bestWayAccumulation;

    public WaySaverServiceImpl(BestWayContainer wayContainer, BestWayAccumulation bestWayAccumulation) {
        this.wayContainer = wayContainer;
        this.bestWayAccumulation = bestWayAccumulation;
    }

    @Override
    @PostMapping(value = "result/saveBestWayCandidate")
    public int saveBestWay(@RequestBody BestWayCandidateDto wayCandidate) {
//        logger.info(String.format("Save best way %s count %d", wayCandidate.getTotalMoney(), wayContainer.getBestWayCandidateDtos().size() + 1));

        return wayContainer.saveBestWay(wayCandidate);
    }

    @Override
    @GetMapping(value = "result/getSuperWay")
    public BestWayCandidateDto getSuperWay() {
        BestWayCandidateDto bestWayCandidateDto = wayContainer.getBestWayCandidateDtos().stream()
                .max(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
                .orElse(null);
        return bestWayCandidateDto;
    }

    @Override
    @GetMapping(value = "result/getWorstWay")
    public BestWayCandidateDto getWorstWay() {
        BestWayCandidateDto bestWayCandidateDto = wayContainer.getBestWayCandidateDtos().stream()
                .min(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
                .orElse(null);
        return bestWayCandidateDto;
    }

    @Override
    @PostMapping(value = "result/savePoint")
    public int savePoint(@RequestBody PointForSaveDto pointForSaveDto) {
        bestWayAccumulation.saveBestWay(pointForSaveDto.getAlgorithm(), pointForSaveDto.getPointName());
        return 0;
    }

    @Override
    @GetMapping(value = "result/getBestWayResult")
    public ConcurrentHashMap<String, CopyOnWriteArrayList<String>> getBestWayResult() {
        final ConcurrentHashMap<String, CopyOnWriteArrayList<String>> result = bestWayAccumulation.getResult();
        result.forEach((key, value) -> {
            logger.info(String.format("------Algorithm %s ---------------------", key));
            final String s1 = value.stream()
                    .reduce((s, s2) -> String.format("%s->%s", s, s2))
                    .get();
            logger.info(s1);
        });

        return result;
    }
}
