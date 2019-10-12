package ru.sberbank.inkass.dto;


import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder
public class PointDto implements Comparable<PointDto> {
    private final String name;
    //    время инкассации
    private final double timeInPoint;
    // признак того что точка является банком куда нужно все отвезти
    private final boolean isBase;
    //    сумма инкассации
    private double sum;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointDto pointDto = (PointDto) o;
        return Double.compare(pointDto.timeInPoint, timeInPoint) == 0 &&
                isBase == pointDto.isBase &&
                name.equals(pointDto.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, timeInPoint, isBase);
    }

    public PointDto copy() {
        return new PointDto(name, timeInPoint, sum, isBase);
    }

    @Override
    public int compareTo(PointDto o) {
        return this.name.compareTo(o.name);
    }
}
