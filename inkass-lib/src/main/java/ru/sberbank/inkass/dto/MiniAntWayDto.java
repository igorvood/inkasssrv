package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class MiniAntWayDto {

    private PointDto bankPoint;

    public MiniAntWayDto() {
        wayPair = new ArrayList<>();
    }
    private double totalTime;
    private double totalMoney;
    private double moneyOnThisTrip;
    private PointDto currentPoint;

    private List<Pair<PointDto, PointDto>> wayPair;

}
