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
import ru.sberbank.inkass.dto.EdgeDto;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.dto.PointForSaveDto;
import ru.sberbank.inkass.dto.ReciveCarDto;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static ru.sberbank.inkass.dto.AntWayDto.nvl;

@RestController
public class RunCalculationAsync {
    private static final Log LOGGER = LogFactory.getLog(RunCalculationAsync.class);

    private final FlagsService flagsService;

    private final CalculationServiceAsync calculationService;
    private final GraphContainer graphContainer;

    private final ConcurrentLinkedDeque<Pair<String, Double>> dequeCars;
    private AtomicLong atomicLong;

    private final CalculationServiceAsync calculationServiceAsync;

    public RunCalculationAsync(FlagsService flagsService, @Qualifier("calculationServiceAsyncImpl") CalculationServiceAsync calculationService, GraphContainer graphContainer, CalculationServiceAsync calculationServiceAsync) {
        this.flagsService = flagsService;
        this.calculationService = calculationService;
        this.graphContainer = graphContainer;
        this.calculationServiceAsync = calculationServiceAsync;
        dequeCars = new ConcurrentLinkedDeque();
        atomicLong = new AtomicLong(0);
    }


    @RequestMapping(value = "/graph/reciveNewGraphSocket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public boolean reciveNewGraphSocket(@RequestBody GraphDto graphDto) {

        List<EdgeDto> norm = new ArrayList<>();

        List<EdgeDto> edgeDtos = graphDto.getEdgeDtos();
        edgeDtos.stream()
                .forEach(edgeDto -> {

                    norm.add(edgeDto);
                    norm.add(new EdgeDto(edgeDto.getTo(), edgeDto.getFrom(), edgeDto.getWayInfo()));
                });
        graphDto.setEdgeDtos(norm);


        LOGGER.info("Graph ->" + graphDto.getEdgeDtos().size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag " + flagsService.getEndCalcFlag().get());
        flagsService.getEndCalcFlag().set(true);
        graphContainer.saveGraph(graphDto);
        return true;
    }

    @RequestMapping(value = "/graph/reciveCarSocket", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    public boolean reciveCarSocket(@RequestBody List<ReciveCarDto> cars) {
        List<ReciveCarDto> collect = cars.stream()
                .map(q -> new ReciveCarDto(q.getCar(), 480 - nvl(q.getTime(), 480D)))
                .collect(Collectors.toList());
//        LOGGER.info("ReciveCarDto ->" + collect.size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag " + flagsService.getEndCalcFlag().get());
        flagsService.getEndCalcFlag().set(true);
        collect.stream()
                .peek(w -> LOGGER.info("======ReciveCarDto ->" + w.getCar() + " time -> " + w.getTime()))
                .forEach(q -> dequeCars.addLast(Pair.of(q.getCar(), q.getTime())));

        return true;
    }

    @Scheduled(fixedRate = 100)
    public void reportCurrentTime1() {
        LOGGER.info("reportCurrentTime ->" + atomicLong.get() + " new Date().getTime() " + new Date().getTime());
        if (atomicLong.get() + 1000 < new Date().getTime() && !flagsService.getReadyForRun().get())
            flagsService.getEndCalcFlag().set(true);
    }

    @Scheduled(fixedRate = 100)
    public void reportCurrentTime() {
        if (dequeCars.size() > 0) {
            flagsService.getEndCalcFlag().set(true);
        }
        if (flagsService.getReadyForRun().get() && dequeCars.size() > 0 && graphContainer.getGraph() != null) {

            flagsService.getEndCalcFlag().set(false);
            LOGGER.info("ReciveCarDto ->" + dequeCars.size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag" + flagsService.getEndCalcFlag().get());
            Pair<String, Double> first = dequeCars.removeFirst();
            LOGGER.info("ReciveCarDto ->" + dequeCars.size() + " getReadyForRun " + flagsService.getReadyForRun().get() + " getEndCalcFlag" + flagsService.getEndCalcFlag().get());
            atomicLong.set(new Date().getTime());
            flagsService.getReadyForRun().set(false);
            calculationServiceAsync.calcWay(first.getKey(), Double.valueOf(nvl(first.getRight(), 0D)), graphContainer.getGraph());
        }
    }

    public PointForSaveDto calc() {
        return null;
    }
}
