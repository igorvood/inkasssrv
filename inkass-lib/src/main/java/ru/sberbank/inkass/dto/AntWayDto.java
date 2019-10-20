package ru.sberbank.inkass.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.util.Assert;

import java.util.*;
import java.util.stream.Collectors;

import static ru.sberbank.inkass.dto.TypePoint.*;

@Getter
@ToString
public class AntWayDto {
    private static final Log LOGGER = LogFactory.getLog(AntWayDto.class);
    private int antNum;
    @Setter
    private double totalTime;
    @Setter
    private double totalMoney;
    @Setter
    private double moneyOnThisTrip;
    @Setter
    private PointDto currentPoint;
    private PointDto bankPoint;

    private List<Double> shipping;
    //    private List<PointDto> way;
    private List<Pair<PointDto, PointDto>> wayPair;
    private Set<PointDto> notVisitedPoint;
    private Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap;


    public AntWayDto(int antNum
            , Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap
            , PointDto bankPoint
            , PointDto currentPoint
            , Set<PointDto> notVisitedPoint
    ) {
        this.antNum = antNum;
        this.roadMap = roadMap;
//                roadMap.entrySet().stream()
//                        .map(e -> Pair.of(
//                                e.getKey(),
//                                new WayInfoDto(e.getValue().getTimeInWay()
//                                        , e.getValue().getPheromone()
//                                        , e.getValue().getTrafficKoef()
//                                )))
//                        .collect(toMap(Pair::getKey, Pair::getValue));
        this.notVisitedPoint = new HashSet<>(notVisitedPoint);
        this.currentPoint = currentPoint;
        this.bankPoint = bankPoint;
        this.shipping = new ArrayList<>();
        this.totalTime = 0L;
        this.totalMoney = 0L;
        this.moneyOnThisTrip = 0L;
        this.wayPair = new ArrayList<>();
    }


    public AntWayDto(int antNum
            , Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap
            , PointDto bankPoint
            , PointDto currentPoint
            , Set<PointDto> notVisitedPoint
            , MiniAntWayDto miniAntWayDto
    ) {
        this(antNum, roadMap, bankPoint, currentPoint, notVisitedPoint);
        this.wayPair = new ArrayList<>(miniAntWayDto.getWayPair());

        this.totalTime = miniAntWayDto.getTotalTime();
        this.moneyOnThisTrip = miniAntWayDto.getMoneyOnThisTrip();
        this.currentPoint = nvl(miniAntWayDto.getCurrentPoint(), this.currentPoint);
        this.bankPoint = nvl(miniAntWayDto.getBankPoint(), this.bankPoint);
    }


    public static <T> T nvl(T currentPoint, T currentPoint1) {
        return currentPoint == null ? currentPoint1 : currentPoint;
    }

    public static Set<PointDto> getNotVisitedPoint(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap, Collection<PointDto> exceptPoint) {
        Assert.notNull(exceptPoint, "exceptPoint must be not null");
        return roadMap.keySet().stream()
                .map(Pair::getKey)
//                .peek(pointDto -> {
//                    if (pointDto.getTypePoint() == GARAGE) {
//                        currentPoint = pointDto;
//                    }
//                    if (pointDto.getTypePoint() == BANK) {
//                        bankPoint = pointDto;
//                    }
//                })
                .filter(pointDto -> INKASS_POINT == pointDto.getTypePoint())
                .filter(pointDto -> !exceptPoint.contains(pointDto))
                .map(PointDto::copy)
                .collect(Collectors.toSet());
    }

    public static Set<PointDto> getNotVisitedPoint(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap) {
        return getNotVisitedPoint(roadMap, new HashSet<>());
    }

    public static PointDto getBankPoint(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap) {
        return getPointByType(roadMap, BANK);
    }

    public static PointDto getGragePoint(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap) {
        return getPointByType(roadMap, GARAGE);
    }

    private static PointDto getPointByType(Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap, TypePoint typePoint) {
        return roadMap.keySet().stream()
                .map(Pair::getKey)
                .filter(pointDto -> typePoint == pointDto.getTypePoint())
                .findFirst().orElse(null);
    }
}
