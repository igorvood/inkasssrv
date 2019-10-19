package ru.sberbank.inkass.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;
import static ru.sberbank.inkass.dto.TypePoint.*;

@Getter
@ToString
public class AntWayDto {

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

    public AntWayDto(int antNum, Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap, MiniAntWayDto miniAntWayDto) {
        this(antNum, roadMap);
        this.wayPair = new ArrayList<>(miniAntWayDto.getWayPair());
        this.totalTime = miniAntWayDto.getTotalTime();
        this.moneyOnThisTrip = miniAntWayDto.getMoneyOnThisTrip();
        this.currentPoint = nvl(miniAntWayDto.getCurrentPoint(), this.currentPoint);
        this.bankPoint = nvl(miniAntWayDto.getBankPoint(), this.bankPoint);
        final List<PointDto> collect = wayPair.stream()
                .map(Pair::getRight)
                .distinct()
                .collect(Collectors.toList());
        notVisitedPoint.removeAll(collect);
    }

    public AntWayDto(int antNum, Map<Pair<PointDto, PointDto>, WayInfoDto> roadMap) {
        this.antNum = antNum;
        this.shipping = new ArrayList<>();
        this.totalTime = 0L;
        this.totalMoney = 0L;
        this.moneyOnThisTrip = 0L;
        this.wayPair = new ArrayList<>();
//        this.currentPoint =
        this.notVisitedPoint =
                roadMap.keySet().stream()
                        .map(Pair::getKey)
                        .peek(pointDto -> {
                            if (pointDto.getTypePoint() == GARAGE) {
                                currentPoint = pointDto;
                            }
                            if (pointDto.getTypePoint() == BANK) {
                                bankPoint = pointDto;
                            }
                        })
                        .filter(pointDto -> INKASS_POINT == pointDto.getTypePoint())
                        .map(PointDto::copy)
                        .collect(Collectors.toSet());
//        this.way = new ArrayList<>();

//        this.way.add(currentPoint);
        this.roadMap = //roadMap;
                roadMap.entrySet().stream()
                        .map(e -> Pair.of(
                                e.getKey(),
                                new WayInfoDto(e.getValue().getTimeInWay()
                                        , e.getValue().getPheromone()
                                        , e.getValue().getTrafficKoef()
                                )))
                        .collect(toMap(Pair::getKey, Pair::getValue));

    }

    private <T> T nvl(T currentPoint, T currentPoint1) {
        return currentPoint == null ? currentPoint1 : currentPoint;
    }

}
