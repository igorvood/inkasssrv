package ru.sberbank.service;

import org.springframework.web.bind.annotation.GetMapping;
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
    @GetMapping(value = "graph/getNewGraph")
    public GraphDto getNewGraph() {
        return fillGraphService.fill(property.getGraphSize());
    }
}
