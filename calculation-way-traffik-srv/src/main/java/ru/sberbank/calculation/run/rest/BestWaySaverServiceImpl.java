package ru.sberbank.calculation.run.rest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import ru.sberbank.inkass.dto.BestWayCandidateDto;
import ru.sberbank.inkass.dto.PointForSaveDto;

@Service
@PropertySource("classpath:connection.properties")
public class BestWaySaverServiceImpl implements BestWaySaverService {
    private static final Logger logger = LoggerFactory.getLogger(BestWaySaverServiceImpl.class);

    @Value("${connect.server}")
    private String server;

    @Value("${connect.server.result.best_list}")
    private String bestListUrl;

    @Value("${connect.server.result.savePoint}")
    private String savePointUrl;

    @Value("${connect.server.result.bestWayResult}")
    private String bestWayResultUrl;


    private final RestTemplate restTemplate;

    public BestWaySaverServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public int saveBestWay(BestWayCandidateDto wayCandidate) {
//        final Integer forObject = restTemplate.getForObject(server + bestListUrl, Integer.class, wayCandidate);
//        return forObject;
        final ResponseEntity<BestWayCandidateDto> requestEntity = new ResponseEntity<BestWayCandidateDto>(wayCandidate, HttpStatus.OK);
        final ResponseEntity<Integer> exchange = restTemplate.exchange(server + bestListUrl, HttpMethod.POST, requestEntity, Integer.class);
        return exchange.getBody();
    }

    @Override
    public void savePoint(PointForSaveDto pointForSaveDto) {
        logger.info("pointForSaveDto ----------------->" + pointForSaveDto.getPointName());
        restTemplate.getForObject(String.format("http://172.30.10.182:3011/result/savePoint?car=%s&point=%s"
                , pointForSaveDto.getCar()
                , pointForSaveDto.getPointName())
                , String.class);

//        final ResponseEntity<PointForSaveDto> requestEntity = new ResponseEntity<>(pointForSaveDto, HttpStatus.OK);
//        final ResponseEntity<String> exchange = restTemplate.exchange("http://172.30.10.129:3011/result/savePoint", HttpMethod.GET, requestEntity, String.class);

//        final Integer forObject = restTemplate.getForObject(server + savePointUrl, Integer.class, algorithm, savePoint);
    }

    @Override
    public void bestWayResult() {
        restTemplate.getForObject(server + bestWayResultUrl, String.class);
    }
}
