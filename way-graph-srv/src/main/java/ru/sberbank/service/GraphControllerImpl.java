package ru.sberbank.service;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.infrastructure.StartPropertyDto;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.service.fill.FillGraphService;

import java.util.logging.Logger;

@RestController
public class GraphControllerImpl implements GraphController {

    private static final Logger logger = Logger.getLogger(GraphControllerImpl.class.getName());

    private final FillGraphService fillGraphService;

    private final StartPropertyDto property;

    public GraphControllerImpl(FillGraphService fillGraphService, StartPropertyDto property) {
        this.fillGraphService = fillGraphService;
        this.property = property;
    }

    @Override
//    @GetMapping(value = "graph/getNewGraph"/*, produces = MediaType.APPLICATION_JSON*/)
    @RequestMapping(value = "/graph/getNewGraph", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public GraphDto getNewGraph() {
        return fillGraphService.fill(property.getGraphSize());
    }
}
