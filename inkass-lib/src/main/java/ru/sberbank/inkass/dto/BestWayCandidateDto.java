package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.commons.lang3.tuple.MutablePair;

import java.util.List;

@Data
@AllArgsConstructor
public class BestWayCandidateDto {
    transient private final List<MutablePair<PointDto, PointDto>> wayPair;
    private final double totalTime;
    private final double totalMoney;

}
