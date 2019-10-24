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
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;
import static ru.sberbank.inkass.dto.AntWayDto.nvl;


@Service
public class CalculationServiceImpl implements CalculationService {

    private static final Log LOGGER = LogFactory.getLog(CalculationServiceImpl.class);

    private final GraphService graphService;

    private final CalcChanceService calcChanceService;

    private final BestWaySaverService bestWaySaverService;

    private final StartPropertyDto prop;


    public CalculationServiceImpl(GraphService graphService, CalcChanceService calcChanceService, BestWaySaverService bestWaySaverService, StartPropertyDto prop) {
        this.graphService = graphService;
        this.calcChanceService = calcChanceService;
        this.bestWaySaverService = bestWaySaverService;
        this.prop = prop;
    }


    @Override
    public void calcWay() {

        final long timeBeg = new Date().getTime();

        final int workingDayCount = prop.getWorkingDayCount();
        final int antCount = prop.getAntCount();

        LOGGER.info("++++++++++++++++++++++++ antCount " + antCount + " day count " + workingDayCount);
        final GraphDto fill = graphService.getNewGraph();

        final double speedTranspirationPheromone = prop.getSpeedTranspirationPheromone();
        MiniAntWayDto currentMiniAntWayDto = new MiniAntWayDto();
        boolean endCalcFlag = true;
        do {
            CopyOnWriteArrayList<MiniAntWayDto> miniAntWayDtos = new CopyOnWriteArrayList<>();
            IntStream.range(0, workingDayCount)
                    .peek(value -> LOGGER.info("Day num " + value))
                    .forEach(value -> {
//                        CopyOnWriteArrayList<BestWayCandidateDto> bestWays = new CopyOnWriteArrayList();
//============================================Дневной цикл==============================
                        final PointDto bankPoint = AntWayDto.getBankPoint(fill.getInfoDtoTreeMap());
                        final PointDto currentPoint = nvl(currentMiniAntWayDto.getCurrentPoint(), AntWayDto.getGragePoint(fill.getInfoDtoTreeMap()));
                        final Set<PointDto> visitedPoint = currentMiniAntWayDto.getWayPair().stream()
                                .map(q -> q.getRight())
                                .collect(toSet());
                        final Set<PointDto> notVisitedPoint = AntWayDto.getNotVisitedPoint(fill.getInfoDtoTreeMap(), visitedPoint);

                        final Map<MutablePair<PointDto, PointDto>, DoubleSummaryStatistics> collect = IntStream.range(0, antCount)
                                .parallel()

                                .mapToObj(i -> new AntWayDto(i, fill.getInfoDtoTreeMap(), bankPoint, currentPoint, notVisitedPoint, currentMiniAntWayDto))
                                .map(q -> calcChanceService.runOneAnt(q))
//                                .peek(antWayDto -> {
//                                    bestWays.add(new BestWayCandidateDto(antWayDto.getTotalTime(), antWayDto.getTotalMoney()));
//                                    if (bestWays.size() % 1000 == 0)
//                                        LOGGER.info(String.format("add best way candidate %d", bestWays.size()));
//                                })
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
//                                    MiniAntWayDto.builder()
//                                            .bankPoint(antWayDto.getBankPoint())
//                                            .wayPair(antWayDto.getWayPair())
//                                            .totalMoney(antWayDto.getTotalMoney())
//                                            .totalTime(antWayDto.getTotalTime())
//                                            .build()
//                                    )
                                )
                                .
                                        flatMap((Function<AntWayDto, Stream<MutablePair<MutablePair<PointDto, PointDto>, Double>>>) antWayDto -> antWayDto.getWayPair().stream()
                                                .map(q -> new MutablePair(q, antWayDto.getTripTelemetry().getTotalMoney())))
                                .collect(groupingBy(MutablePair::getLeft, mapping(MutablePair::getRight, summarizingDouble(value1 -> value1))));
//                        final BestWayCandidateDto bestWayCandidateDto = bestWays.stream()
//                                .max(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
//                                .get();
//                        final BestWayCandidateDto worstWayCandidateDto = bestWays.stream()
//                                .min(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
//                                .get();
//                        Assert.isTrue(bestWayCandidateDto.getTotalMoney() >= worstWayCandidateDto.getTotalMoney(), () -> "плохой алгоритм сравнения");
//                        bestWaySaverService.saveBestWay(bestWayCandidateDto);
                        fill.getEdgeDtos().stream()
                                .parallel()
                                .forEach(q -> {
                                    final DoubleSummaryStatistics doubleSummaryStatistics = collect.get(Pair.of(q.getFrom(), q.getTo()));
                                    if (doubleSummaryStatistics != null)
                                        q.getWayInfo().setPheromone((q.getWayInfo().getPheromone() * speedTranspirationPheromone + doubleSummaryStatistics.getSum()));
                                });

                        LOGGER.info(collect.size());
                    });
            final MiniAntWayDto miniAntWayDto = miniAntWayDtos.stream()
                    .max(Comparator.comparingDouble(MiniAntWayDto::getTotalMoney))
                    .get();

            endCalcFlag = currentMiniAntWayDto.getWayPair().size() == miniAntWayDto.getWayPair().size();
            if (!endCalcFlag) {
                Pair<PointDto, PointDto> pointDtoPointDtoPair = null;
                try {
                    pointDtoPointDtoPair = miniAntWayDto.getWayPair().get(currentMiniAntWayDto.getWayPair().size());
                } catch (IndexOutOfBoundsException e) {
                    System.out.println(e);
                }
                final WayInfoDto wayInfoDto = fill.getInfoDtoTreeMap().get(pointDtoPointDtoPair);
                final PointDto nextPoint = pointDtoPointDtoPair.getRight();

                currentMiniAntWayDto.getWayPair().add(pointDtoPointDtoPair);

                currentMiniAntWayDto.setCurrentPoint(nextPoint);
                currentMiniAntWayDto.setTotalMoney(currentMiniAntWayDto.getTotalMoney() + nextPoint.getSum());
                currentMiniAntWayDto.setTotalTime(currentMiniAntWayDto.getTotalTime() + wayInfoDto.getTimeInWay() + nextPoint.getTimeInPoint());
                double moneyOnThisTrip = Util.calcMoneyOnThisTrip(nextPoint, currentMiniAntWayDto.getMoneyOnThisTrip(), miniAntWayDto.getBankPoint());
                currentMiniAntWayDto.setMoneyOnThisTrip(moneyOnThisTrip);
                currentMiniAntWayDto.setBankPoint(miniAntWayDto.getBankPoint());
//                bestWaySaverService.savePoint(String.valueOf(timeBeg), nextPoint.getName());
            }
        } while (!endCalcFlag);
        bestWaySaverService.bestWayResult();
        LOGGER.debug("++++++++++++++++++++++++" /*+ collect.size()*/);
        final long l = new Date().getTime() - timeBeg;

        LOGGER.debug("total time " + (double) l / 1000L + " time per 1000 ants " + l / (double) workingDayCount / 1000L / ((double) antCount / 1000L));
    }
}
