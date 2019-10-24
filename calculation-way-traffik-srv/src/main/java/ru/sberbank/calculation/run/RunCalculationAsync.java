package ru.sberbank.calculation.run;


import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.sberbank.calculation.run.container.GraphContainer;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.dto.PointForSaveDto;
import ru.sberbank.inkass.dto.ReciveCarDto;

import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;

import static ru.sberbank.inkass.dto.AntWayDto.nvl;

@RestController
public class RunCalculationAsync {
    private static final Log LOGGER = LogFactory.getLog(RunCalculationAsync.class);

    private final FlagsService flagsService;

    private final CalculationServiceAsync calculationService;
    private final GraphContainer graphContainer;

    private final ConcurrentLinkedDeque<Pair<String, Long>> dequeCars;

    private final CalculationServiceAsync calculationServiceAsync;

    public RunCalculationAsync(FlagsService flagsService, @Qualifier("calculationServiceAsyncImpl") CalculationServiceAsync calculationService, GraphContainer graphContainer, CalculationServiceAsync calculationServiceAsync) {
        this.flagsService = flagsService;
        this.calculationService = calculationService;
        this.graphContainer = graphContainer;
        this.calculationServiceAsync = calculationServiceAsync;
        dequeCars = new ConcurrentLinkedDeque();
    }


    @RequestMapping(value = "/graph/reciveNewGraphSocket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public boolean reciveNewGraphSocket(@RequestBody GraphDto graphDto) {
        LOGGER.info("Graph ->" + graphDto.getEdgeDtos().size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag" + flagsService.getEndCalcFlag().get());
        flagsService.getEndCalcFlag().set(true);
        graphContainer.saveGraph(graphDto);
        return true;
    }

    @RequestMapping(value = "/graph/reciveCarSocket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public boolean reciveCarSocket(@RequestBody List<ReciveCarDto> cars) {
        LOGGER.info("ReciveCarDto ->" + cars.size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag" + flagsService.getEndCalcFlag().get());
        flagsService.getEndCalcFlag().set(true);
        cars.forEach(q -> dequeCars.addLast(Pair.of(q.getCar(), q.getTime())));

        return true;
    }


    @Scheduled(fixedRate = 100)
    public void reportCurrentTime() {
        if (dequeCars.size() > 0) {
            flagsService.getEndCalcFlag().set(true);
        }
        if (flagsService.getReadyForRun().get() && dequeCars.size() > 0 && graphContainer.getGraph() != null) {
            flagsService.getEndCalcFlag().set(false);
            LOGGER.info("ReciveCarDto ->" + dequeCars.size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag" + flagsService.getEndCalcFlag().get());
            Pair<String, Long> first = dequeCars.removeFirst();
            LOGGER.info("ReciveCarDto ->" + dequeCars.size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag" + flagsService.getEndCalcFlag().get());
            flagsService.getReadyForRun().set(false);
            calculationServiceAsync.calcWay(first.getKey(), Double.valueOf(nvl(first.getRight(), 0L)), graphContainer.getGraph());
        }
    }

    public PointForSaveDto calc() {
        return null;
    }
}
