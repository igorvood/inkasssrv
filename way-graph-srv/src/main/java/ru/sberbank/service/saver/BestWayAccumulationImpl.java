package ru.sberbank.service.saver;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.sberbank.inkass.dto.AntWayDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Service
public class BestWayAccumulationImpl implements BestWayAccumulation {
    private static final Logger logger = Logger.getLogger(BestWayAccumulationImpl.class.getName());
    private final GraphContainer graphContainer;
    private ConcurrentHashMap<String, Pair<CopyOnWriteArrayList<String>, AntWayDto>> bestWays;

    public BestWayAccumulationImpl(GraphContainer graphContainer) {
        this.graphContainer = graphContainer;
        this.bestWays = new ConcurrentHashMap<>();
    }

    @Override
    public void reset() {
        this.bestWays = new ConcurrentHashMap<>();
        logger.info(String.format("reset best way accumulation"));
    }

    @Override
    public int saveBestWay(String algorithm, String pointName) {
        final Pair<CopyOnWriteArrayList<String>, AntWayDto> antWayDtoPair = bestWays.get(algorithm);
        CopyOnWriteArrayList<String> strings = antWayDtoPair.getLeft();
        if (strings == null) {
            strings = new CopyOnWriteArrayList<>();
            bestWays.put(algorithm, Pair.of(strings, null));
        }
        strings.add(pointName);
        logger.info(String.format("put point %s for algorithm %s", pointName, algorithm));
        return 0;
    }

    @Override
    public ConcurrentHashMap<String, CopyOnWriteArrayList<String>> getResult() {
        return bestWays;
    }
}
