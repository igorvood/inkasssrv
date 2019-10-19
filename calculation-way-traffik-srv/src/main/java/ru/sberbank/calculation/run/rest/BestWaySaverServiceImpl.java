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

@Service
@PropertySource("classpath:connection.properties")
public class BestWaySaverServiceImpl implements BestWaySaverService {
    private static final Logger logger = LoggerFactory.getLogger(BestWaySaverServiceImpl.class);

    @Value("${connect.server}")
    private String server;

    @Value("${connect.server.result.best_list}")
    private String bestList;

    @Value("${connect.server.result.savePoint}")
    private String savePoint;


    private final RestTemplate restTemplate;

    public BestWaySaverServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public int saveBestWay(BestWayCandidateDto wayCandidate) {
//        final Integer forObject = restTemplate.getForObject(server + bestList, Integer.class, wayCandidate);
//        return forObject;
        final ResponseEntity<BestWayCandidateDto> requestEntity = new ResponseEntity<BestWayCandidateDto>(wayCandidate, HttpStatus.OK);
        final ResponseEntity<Integer> exchange = restTemplate.exchange(server + bestList, HttpMethod.POST, requestEntity, Integer.class);
        return exchange.getBody();
    }

    @Override
    public void savePoint(String algorithm, String savePoint) {
        final Integer forObject = restTemplate.getForObject(server + bestList, Integer.class, algorithm, savePoint);
    }
}
