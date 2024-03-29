package ru.sberbank.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.infrastructure.FileService;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.property.StartPropertyDto;
import ru.sberbank.service.fill.FillGraphService;
import ru.sberbank.service.saver.BestWayAccumulation;
import ru.sberbank.service.saver.BestWayContainer;
import ru.sberbank.service.saver.GraphContainer;

import java.util.logging.Logger;

@RestController
public class GraphControllerImpl implements GraphController {

    private static final Logger logger = Logger.getLogger(GraphControllerImpl.class.getName());

    private final FillGraphService fillGraphService;

    private final StartPropertyDto property;

    private final GraphContainer graphContainer;

    private final BestWayContainer bestWayContainer;

    private final BestWayAccumulation bestWayAccumulation;

    public GraphControllerImpl(FillGraphService fillGraphService, StartPropertyDto property, GraphContainer graphContainer, BestWayContainer bestWayContainer, BestWayAccumulation bestWayAccumulation) {
        this.fillGraphService = fillGraphService;
        this.property = property;
        this.graphContainer = graphContainer;
        this.bestWayContainer = bestWayContainer;
        this.bestWayAccumulation = bestWayAccumulation;
    }

    @Override
    @RequestMapping(value = "/graph/getNewGraph", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public GraphDto getNewGraph() {
        logger.info("get new graph");
        GraphDto fill = fillGraphService.fill(property.getGraphSize());

        final Gson gson = new GsonBuilder().setPrettyPrinting().create();

//        final String s = gson.toJson(fill);
//        FileService.write("outGraph.gson", s);
//        ------------------------------------

        if (graphContainer.getGraph() == null) {
            logger.info("Start read file");
            final String read = FileService.read("outGraph.gson");
            logger.info("Start parse file");
            fill = gson.fromJson(read, GraphDto.class);
            logger.info("End parse file");
        } else fill = graphContainer.getGraph();

        graphContainer.saveGraph(fill);
        bestWayContainer.reset();
        bestWayAccumulation.reset();

        return fill;
    }

    @Override
    @RequestMapping(value = "/graph/getRefreshedGraph", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public GraphDto getRefreshedGraph(@RequestBody PointDto registrarPoint) {
        return graphContainer.getRefreshGraph();
    }

    @RequestMapping(value = "/graph/getProp", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public StartPropertyDto getProp() {
        return property;
    }

    @Override
    @RequestMapping(value = "/graph/reciveNewGraphSocket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.PUT)
    public boolean reciveNewGraphSocket(@RequestBody GraphDto graphDto) {
        graphContainer.saveGraph(graphDto);
        return true;
    }

    @Override
//    @RequestMapping(value = "/graph/getNewGraph", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public GraphDto getNewGraphSocket(GraphDto graphDto) {
        return graphContainer.getGraph();
    }
}

