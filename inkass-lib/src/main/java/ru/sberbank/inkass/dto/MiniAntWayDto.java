package ru.sberbank.inkass.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

@Getter
@Setter
@Builder
public class MiniAntWayDto {
    @Setter
    private double totalTime;
    @Setter
    private double totalMoney;
    @Setter
    private double moneyOnThisTrip;
    @Setter
    private PointDto currentPoint;
    private PointDto bankPoint;
    private List<Pair<PointDto, PointDto>> wayPair;

}
