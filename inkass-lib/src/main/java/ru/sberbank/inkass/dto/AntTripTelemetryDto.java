package ru.sberbank.inkass.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AntTripTelemetryDto {
    private double totalTime;
    private double totalMoney;
    private double moneyOnThisTrip;
}
