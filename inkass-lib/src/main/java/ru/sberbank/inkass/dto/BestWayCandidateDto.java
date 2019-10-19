package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class BestWayCandidateDto {
    //    transient private final List<MutablePair<PointDto, PointDto>> wayPair;
    private final double totalTime;
    private final double totalMoney;

}
