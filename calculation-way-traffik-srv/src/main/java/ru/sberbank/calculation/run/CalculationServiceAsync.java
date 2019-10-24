package ru.sberbank.calculation.run;

import ru.sberbank.inkass.dto.GraphDto;

public interface CalculationServiceAsync {
    void calcWay(String car, Double totalTime, GraphDto fill);

}
