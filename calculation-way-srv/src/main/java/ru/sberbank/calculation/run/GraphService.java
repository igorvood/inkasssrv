package ru.sberbank.calculation.run;

import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.property.StartPropertyDto;

public interface GraphService {

    GraphDto getNewGraph();

    StartPropertyDto getProp();
}
