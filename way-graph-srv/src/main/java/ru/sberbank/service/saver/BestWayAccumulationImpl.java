package ru.sberbank.service.saver;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

@Service
public class BestWayAccumulationImpl implements BestWayAccumulation {
    private static final Logger logger = Logger.getLogger(BestWayAccumulationImpl.class.getName());
    private ConcurrentHashMap<String, CopyOnWriteArrayList<String>> bestWays;

    public BestWayAccumulationImpl() {
        this.bestWays = new ConcurrentHashMap<>();
    }

    @Override
    public void reset() {
        this.bestWays = new ConcurrentHashMap<>();
    }

    @Override
    public int saveBestWay(String algorithm, String pointName) {
        CopyOnWriteArrayList<String> strings = bestWays.get(algorithm);
        if (strings == null) {
            strings = new CopyOnWriteArrayList<>();
            bestWays.put(algorithm, strings);
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
