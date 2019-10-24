package ru.sberbank.calculation.run.container;

import ru.sberbank.inkass.dto.GraphDto;

public interface GraphContainer {

    void saveGraph(GraphDto graphDto);

    GraphDto getRefreshGraph();

    GraphDto getGraph();
}
