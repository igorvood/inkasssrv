package ru.sberbank.service;

import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.dto.PointDto;

public interface GraphController {

    GraphDto getNewGraph();

    GraphDto getRefreshedGraph(PointDto registrarPoint);
//-------------------------------------

    boolean reciveNewGraphSocket(GraphDto graphDto);

    GraphDto getNewGraphSocket(GraphDto graphDto);


}
