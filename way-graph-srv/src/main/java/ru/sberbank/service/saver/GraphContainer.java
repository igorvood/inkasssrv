package ru.sberbank.service.saver;

import ru.sberbank.inkass.dto.GraphDto;

public interface GraphContainer {

    void saveGraph(GraphDto graphDto);

    GraphDto getRefreshGraph();
}
