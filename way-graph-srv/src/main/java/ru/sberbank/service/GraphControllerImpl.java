package ru.sberbank.service;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.property.StartPropertyDto;
import ru.sberbank.service.fill.FillGraphService;
import ru.sberbank.service.saver.GraphContainer;

import java.util.logging.Logger;

@RestController
public class GraphControllerImpl implements GraphController {

    private static final Logger logger = Logger.getLogger(GraphControllerImpl.class.getName());

    private final FillGraphService fillGraphService;

    private final StartPropertyDto property;

    private final GraphContainer graphContainer;

    public GraphControllerImpl(FillGraphService fillGraphService, StartPropertyDto property, GraphContainer graphContainer) {
        this.fillGraphService = fillGraphService;
        this.property = property;
        this.graphContainer = graphContainer;
    }

    @Override
    @RequestMapping(value = "/graph/getNewGraph", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public GraphDto getNewGraph() {
        logger.info("get new graph");
        final GraphDto fill = fillGraphService.fill(property.getGraphSize());
        graphContainer.saveGraph(fill);
        return fill;
    }

    @Override
    @RequestMapping(value = "/graph/getRefreshedGraph", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public GraphDto getRefreshedGraph() {
        return graphContainer.getRefreshGraph();
    }

    @RequestMapping(value = "/graph/getProp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public StartPropertyDto getProp() {
        return property;
    }

}

