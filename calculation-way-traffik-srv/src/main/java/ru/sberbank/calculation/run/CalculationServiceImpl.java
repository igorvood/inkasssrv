package ru.sberbank.calculation.run;

import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import ru.sberbank.calculation.run.rest.BestWaySaverService;
import ru.sberbank.calculation.run.rest.GraphService;
import ru.sberbank.inkass.dto.*;
import ru.sberbank.inkass.function.Util;
import ru.sberbank.inkass.property.StartPropertyDto;

import java.util.Comparator;
import java.util.Date;
import java.util.DoubleSummaryStatistics;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static java.util.stream.Collectors.*;


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
        final GraphDto fill = graphService.getNewGraph();

        final long timeBeg = new Date().getTime();

        LOGGER.info("++++++++++++++++++++++++");
        final int workingDayCount = prop.getWorkingDayCount();
        final int antCount = prop.getAntCount();
        final double speedTranspirationPheromone = prop.getSpeedTranspirationPheromone();
        MiniAntWayDto currentMiniAntWayDto = new MiniAntWayDto();

        CopyOnWriteArrayList<MiniAntWayDto> miniAntWayDtos = new CopyOnWriteArrayList<>();
        IntStream.range(0, workingDayCount)
//                .peek(value -> LOGGER.debug("Day num " + value))
                .forEach(value -> {
                    CopyOnWriteArrayList<BestWayCandidateDto> bestWays = new CopyOnWriteArrayList();

                    final Map<MutablePair<PointDto, PointDto>, DoubleSummaryStatistics> collect = IntStream.range(0, antCount)
                            .parallel()
                            .mapToObj(i -> new AntWayDto(fill.getInfoDtoTreeMap()))
                            .map(q -> calcChanceService.runOneAnt(q))
                            .peek(antWayDto -> {
//                                List<MutablePair<PointDto, PointDto>> l = new ArrayList<>();
//                                antWayDto.getWayPair().forEach(q->l.add(MutablePair.of(q.getLeft(),q.getRight())));
                                bestWays.add(new BestWayCandidateDto(antWayDto.getTotalTime(), antWayDto.getTotalMoney()));
                                if (bestWays.size() % 1000 == 0)
                                    LOGGER.info(String.format("add best way candidate %d", bestWays.size()));
                            })
                            .peek(antWayDto -> miniAntWayDtos.add(
                                    new MiniAntWayDto(antWayDto.getBankPoint()
                                            , antWayDto.getTotalTime()
                                            , antWayDto.getTotalMoney()
                                            , antWayDto.getMoneyOnThisTrip()
                                            , null
                                            , antWayDto.getWayPair()
                                    ))

//                                    MiniAntWayDto.builder()
//                                            .bankPoint(antWayDto.getBankPoint())
//                                            .wayPair(antWayDto.getWayPair())
//                                            .totalMoney(antWayDto.getTotalMoney())
//                                            .totalTime(antWayDto.getTotalTime())
//                                            .build()
//                                    )
                            )
                            .flatMap((Function<AntWayDto, Stream<MutablePair<MutablePair<PointDto, PointDto>, Double>>>) antWayDto -> antWayDto.getWayPair().stream()
                                    .map(q -> new MutablePair(q, antWayDto.getTotalMoney())))
                            .collect(groupingBy(MutablePair::getLeft, mapping(MutablePair::getRight, summarizingDouble(value1 -> value1))));
                    final BestWayCandidateDto bestWayCandidateDto = bestWays.stream()
                            .max(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
                            .get();
                    final BestWayCandidateDto worstWayCandidateDto = bestWays.stream()
                            .min(Comparator.comparingDouble(BestWayCandidateDto::getTotalMoney))
                            .get();
                    Assert.isTrue(bestWayCandidateDto.getTotalMoney() >= worstWayCandidateDto.getTotalMoney(), () -> "плохой алгоритм сравнения");
                    bestWaySaverService.saveBestWay(bestWayCandidateDto);
                    fill.getEdgeDtos().stream()
                            .parallel()
                            .forEach(q -> {
                                final DoubleSummaryStatistics doubleSummaryStatistics = collect.get(Pair.of(q.getFrom(), q.getTo()));
                                if (doubleSummaryStatistics != null)
                                    q.getWayInfo().setPheromone((q.getWayInfo().getPheromone() * speedTranspirationPheromone + doubleSummaryStatistics.getSum()));
                            });

                    LOGGER.debug(collect.size());
                });
        final MiniAntWayDto miniAntWayDto = miniAntWayDtos.stream()
                .max(Comparator.comparingDouble(MiniAntWayDto::getTotalMoney))
                .get();

        final Pair<PointDto, PointDto> pointDtoPointDtoPair = miniAntWayDto.getWayPair().get(currentMiniAntWayDto.getWayPair().size());
        final WayInfoDto wayInfoDto = fill.getInfoDtoTreeMap().get(pointDtoPointDtoPair);
        final PointDto nextPoint = pointDtoPointDtoPair.getRight();

        currentMiniAntWayDto.getWayPair().add(pointDtoPointDtoPair);

        currentMiniAntWayDto.setCurrentPoint(nextPoint);
        currentMiniAntWayDto.setTotalMoney(currentMiniAntWayDto.getTotalMoney() + nextPoint.getSum());
        currentMiniAntWayDto.setTotalTime(currentMiniAntWayDto.getTotalTime() + wayInfoDto.getTimeInWay() + nextPoint.getTimeInPoint());
        double moneyOnThisTrip = Util.calcMoneyOnThisTrip(nextPoint, currentMiniAntWayDto.getMoneyOnThisTrip(), miniAntWayDto.getBankPoint());
        currentMiniAntWayDto.setMoneyOnThisTrip(moneyOnThisTrip);
        currentMiniAntWayDto.setBankPoint(miniAntWayDto.getBankPoint());
        bestWaySaverService.savePoint(String.valueOf(timeBeg), nextPoint.getName());

        LOGGER.debug("++++++++++++++++++++++++" /*+ collect.size()*/);
        final long l = new Date().getTime() - timeBeg;

        LOGGER.debug("total time " + (double) l / 1000L + " time per 1000 ants " + l / (double) workingDayCount / 1000L / ((double) antCount / 1000L));
    }
}
