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

    public GraphControllerImpl(FillGraphService fillGraphService, StartPropertyDto property, GraphContainer graphContainer, BestWayContainer bestWayContainer) {
        this.fillGraphService = fillGraphService;
        this.property = property;
        this.graphContainer = graphContainer;
        this.bestWayContainer = bestWayContainer;
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

        final String read = FileService.read("outGraph.gson");
        fill = gson.fromJson(read, GraphDto.class);

        graphContainer.saveGraph(fill);
        bestWayContainer.reset();
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

}

