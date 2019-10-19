package ru.sberbank.service.saver;

import org.springframework.stereotype.Service;
import ru.sberbank.inkass.dto.BestWayCandidateDto;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Service
public class BestWayContainerImpl implements BestWayContainer {
    private static final Logger logger = Logger.getLogger(BestWayContainerImpl.class.getName());
    private CopyOnWriteArrayList<BestWayCandidateDto> bestWayCandidateDtos;

    public BestWayContainerImpl() {
        bestWayCandidateDtos = new CopyOnWriteArrayList<>();
    }

    @Override
    public void reset() {
        logger.info("Reset best way");
        bestWayCandidateDtos = new CopyOnWriteArrayList<>();
    }

    @Override
    public int saveBestWay(BestWayCandidateDto wayCandidate) {
        bestWayCandidateDtos.add(wayCandidate);
        return bestWayCandidateDtos.size();
    }

    public CopyOnWriteArrayList<BestWayCandidateDto> getBestWayCandidateDtos() {
        return bestWayCandidateDtos;
    }

}
