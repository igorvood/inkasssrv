package ru.sberbank.calculation.run;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import ru.sberbank.calculation.run.rest.BestWaySaverService;
import ru.sberbank.calculation.run.rest.GraphService;
import ru.sberbank.inkass.dto.*;
import ru.sberbank.inkass.function.Util;
import ru.sberbank.inkass.property.StartPropertyDto;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static ru.sberbank.inkass.dto.AntWayDto.nvl;


@Service
public class CalculationServiceAsyncImpl implements CalculationServiceAsync {

    private static final Log LOGGER = LogFactory.getLog(CalculationServiceAsyncImpl.class);

    private final GraphService graphService;

    private final CalcChanceService calcChanceService;

    private final BestWaySaverService bestWaySaverService;

    private final StartPropertyDto prop;
    private final int workingDayCount;
    private final int antCount;
    private final double speedTranspirationPheromone;
    private final FlagsService flagsService;
    private ConcurrentMap<String, MiniAntWayDto> miniAntWayDtoConcurrentHashMap;

    public CalculationServiceAsyncImpl(GraphService graphService, CalcChanceService calcChanceService, BestWaySaverService bestWaySaverService, StartPropertyDto prop, FlagsService flagsService) {
        this.graphService = graphService;
        this.calcChanceService = calcChanceService;
        this.bestWaySaverService = bestWaySaverService;
        this.prop = prop;
        this.miniAntWayDtoConcurrentHashMap = new ConcurrentHashMap<>();
        this.flagsService = flagsService;

//-----------------------------------
        workingDayCount = prop.getWorkingDayCount();
        antCount = prop.getAntCount();
        speedTranspirationPheromone = prop.getSpeedTranspirationPheromone();
    }

    public void calcWay(String car, Double totalTime, GraphDto fill) {

        LOGGER.info("++++++++++++++++++++++++ antCount " + antCount + " day count " + workingDayCount);

        CopyOnWriteArrayList<MiniAntWayDto> miniAntWayDtos = new CopyOnWriteArrayList<>();
        do {
            final PointDto bankPoint = AntWayDto.getBankPoint(fill.getInfoDtoTreeMap());

            Set<PointDto> visitedPoint = miniAntWayDtoConcurrentHashMap.entrySet().stream()
                    .map(q -> q.getValue().getWayPair())
                    .flatMap((Function<List<Pair<PointDto, PointDto>>, Stream<PointDto>>) pairs -> pairs.stream().map(w -> w.getRight()))
                    .collect(toSet());

            MiniAntWayDto miniAntWayDto = miniAntWayDtoConcurrentHashMap.get(car);
            if (miniAntWayDto == null) {
                miniAntWayDto = new MiniAntWayDto(bankPoint, 0L, 0L, 0L, AntWayDto.getGragePoint(fill.getInfoDtoTreeMap()), new ArrayList<>());
                miniAntWayDtoConcurrentHashMap.put(car, miniAntWayDto);
            }

            final MiniAntWayDto miniAntWayDto_____ = miniAntWayDto;

            final PointDto currentPoint = nvl(miniAntWayDto.getCurrentPoint(), AntWayDto.getGragePoint(fill.getInfoDtoTreeMap()));
            final Set<PointDto> notVisitedPoint = AntWayDto.getNotVisitedPoint(fill.getInfoDtoTreeMap(), visitedPoint);
            miniAntWayDto.setTotalTime(nvl(nvl(totalTime, miniAntWayDto.getTotalTime()), 0D));

            final Map<MutablePair<PointDto, PointDto>, DoubleSummaryStatistics> collect = IntStream.range(0, 2000)
                    .parallel()
                    .mapToObj(i -> new AntWayDto(i, fill.getInfoDtoTreeMap(), bankPoint, currentPoint, notVisitedPoint, miniAntWayDto_____))
                    .map(q -> calcChanceService.runOneAnt(q))
                    .peek(antWayDto -> {
                                miniAntWayDtos.add(
                                        new MiniAntWayDto(antWayDto.getBankPoint()
                                                , antWayDto.getTripTelemetry().getTotalTime()
                                                , antWayDto.getTripTelemetry().getTotalMoney()
                                                , antWayDto.getTripTelemetry().getMoneyOnThisTrip()
                                                , null
                                                , antWayDto.getWayPair()
                                        ));
                                if (miniAntWayDtos.size() % 1000 == 0)
                                    LOGGER.info(String.format("add best way candidate %d", miniAntWayDtos.size()));
                            }
                    )
                    .
                            flatMap((Function<AntWayDto, Stream<MutablePair<MutablePair<PointDto, PointDto>, Double>>>) antWayDto -> antWayDto.getWayPair().stream()
                                    .map(q -> new MutablePair(q, antWayDto.getTripTelemetry().getTotalMoney())))
                    .collect(groupingBy(MutablePair::getLeft, mapping(MutablePair::getRight, summarizingDouble(value1 -> value1))));
            fill.getEdgeDtos().stream()
                    .parallel()
                    .forEach(q -> {
                        final DoubleSummaryStatistics doubleSummaryStatistics = collect.get(Pair.of(q.getFrom(), q.getTo()));
                        if (doubleSummaryStatistics != null)
                            q.getWayInfo().setPheromone((q.getWayInfo().getPheromone() * speedTranspirationPheromone + doubleSummaryStatistics.getSum()));
                    });


            if (!flagsService.getEndCalcFlag().get()) {
                final MiniAntWayDto miniAntWayDto111111111 = miniAntWayDtos.stream()
                        .max(Comparator.comparingDouble(MiniAntWayDto::getTotalMoney))
                        .get();

                Pair<PointDto, PointDto> pointDtoPointDtoPair = null;
                try {
                    pointDtoPointDtoPair = miniAntWayDto111111111.getWayPair().get(miniAntWayDto_____.getWayPair().size());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }
                final WayInfoDto wayInfoDto = fill.getInfoDtoTreeMap().get(pointDtoPointDtoPair);
                final PointDto nextPoint = pointDtoPointDtoPair.getRight();

                miniAntWayDto_____.getWayPair().add(pointDtoPointDtoPair);

                miniAntWayDto_____.setCurrentPoint(nextPoint);
                miniAntWayDto_____.setTotalMoney(miniAntWayDto_____.getTotalMoney() + nextPoint.getSum());
                miniAntWayDto_____.setTotalTime(miniAntWayDto_____.getTotalTime() + wayInfoDto.getTimeInWay() + nextPoint.getTimeInPoint());
                double moneyOnThisTrip = Util.calcMoneyOnThisTrip(nextPoint, miniAntWayDto_____.getMoneyOnThisTrip(), miniAntWayDto111111111.getBankPoint());
                miniAntWayDto_____.setMoneyOnThisTrip(moneyOnThisTrip);
                miniAntWayDto_____.setBankPoint(miniAntWayDto111111111.getBankPoint());
                bestWaySaverService.savePoint(new PointForSaveDto("vood", car, nextPoint.getName(), false));
            }


        } while (!flagsService.getEndCalcFlag().get());

        flagsService.getReadyForRun().set(true);

    }


}
