package ru.sberbank.service.saver;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;
import ru.sberbank.inkass.dto.AntTripTelemetryDto;
import ru.sberbank.inkass.dto.EdgeDto;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

import static ru.sberbank.inkass.dto.TypePoint.GARAGE;

@Service
public class BestWayAccumulationImpl implements BestWayAccumulation {
    private static final Logger logger = Logger.getLogger(BestWayAccumulationImpl.class.getName());
    private final GraphContainer graphContainer;
    private ConcurrentHashMap<String, Pair<CopyOnWriteArrayList<String>, AntTripTelemetryDto>> bestWays;

    public BestWayAccumulationImpl(GraphContainer graphContainer) {
        this.graphContainer = graphContainer;
        this.bestWays = new ConcurrentHashMap<>();
    }

    @Override
    public void reset() {
        this.bestWays = new ConcurrentHashMap<>();
        logger.info("reset best way accumulation");
    }

    @Override
    public int saveBestWay(String algorithm, String pointName) {
        final Pair<CopyOnWriteArrayList<String>, AntTripTelemetryDto> antWayDtoPair = bestWays.get(algorithm);
        CopyOnWriteArrayList<String> points = antWayDtoPair.getLeft();
        if (points == null) {
            final EdgeDto edgeDto1 = graphContainer.getGraph().getEdgeDtos().stream()
                    .filter(edgeDto -> edgeDto.getFrom().getTypePoint() == GARAGE && edgeDto.getTo().getName().equals(pointName))
                    .findFirst()
                    .orElse(null);
            points = new CopyOnWriteArrayList<>();
            AntTripTelemetryDto build = AntTripTelemetryDto.builder()
                    .build();
//            new AntWayDto(bestWays.size())
            bestWays.put(algorithm, Pair.of(points, null));
        }
        points.add(pointName);
        logger.info(String.format("put point %s for algorithm %s", pointName, algorithm));
        return 0;
    }

    @Override
    public ConcurrentHashMap<String, Pair<CopyOnWriteArrayList<String>, AntTripTelemetryDto>> getResult() {

        return bestWays;

    }
}
