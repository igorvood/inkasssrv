package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import static java.lang.Math.pow;

@Data
@AllArgsConstructor
public class WayInfoDto {

    private final double timeInWay;

    private double pheromone;

    public final double getWeightWay() {
        return 1 / timeInWay;
    }

    public final double getPheromone() {
        return pow(pheromone, 1);
    }

    public final double getComplexWeight() {
        return getWeightWay() * getPheromone();
    }


}
