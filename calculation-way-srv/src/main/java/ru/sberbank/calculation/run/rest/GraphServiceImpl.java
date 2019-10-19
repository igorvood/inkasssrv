package ru.sberbank.calculation.run.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.property.StartPropertyDto;

@Service
@PropertySource("classpath:connection.properties")
public class GraphServiceImpl implements GraphService {

    @Value("${connect.server}")
    private String server;

    @Value("${connect.server.graph.new}")
    private String newGraph;

    @Value("${connect.server.graph.prop}")
    private String graphProp;


    private static final Logger logger = LoggerFactory.getLogger(GraphServiceImpl.class);

    private final RestTemplate restTemplate;

    public GraphServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public GraphDto getNewGraph() {
        final GraphDto graphDto = restTemplate.getForObject(server + newGraph, GraphDto.class);
        logger.info(String.valueOf(graphDto.getEdgeDtos().size()));
        return graphDto;
    }

    public StartPropertyDto getProp() {
        final StartPropertyDto propertyDto = restTemplate.getForObject(server + graphProp, StartPropertyDto.class);
        logger.info(String.valueOf(propertyDto));
        return propertyDto;
    }

}
