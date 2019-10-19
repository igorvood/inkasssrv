package ru.sberbank.service.saver;

import org.springframework.stereotype.Service;
import ru.sberbank.inkass.dto.GraphDto;

@Service
public class GraphContainerImpl implements GraphContainer {

    private GraphDto graphDto;

    @Override
    public void saveGraph(GraphDto graphDto) {
        this.graphDto = graphDto;
    }

    @Override
    public GraphDto getRefreshGraph() {
        graphDto.getEdgeDtos()
                .forEach(e -> e.getWayInfo().setTrafficKoef(Math.random() * 2));
        return graphDto;
    }

    @Override
    public GraphDto getGraph() {
        return graphDto;
    }
}
