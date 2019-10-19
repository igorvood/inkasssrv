package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WayInfoDto {

    private double timeInWay;

    private double pheromone;

    private double trafficKoef;

    public final double getWeightWay() {
        return 1 / timeInWay;
    }

    public final double getPheromone() {
        return pheromone;
//        return pow(pheromone, 1);
    }

    public final double getComplexWeight(int antNum) {
        final double v = antNum % 2 == 0 ? getPheromone() : 1;
        return getWeightWay() * v * getTrafficKoef();
    }


}
