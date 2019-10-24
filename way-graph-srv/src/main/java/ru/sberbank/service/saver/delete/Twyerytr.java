package ru.sberbank.service.saver.delete;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Component
public class Twyerytr {


    private final RestTemplate restTemplate;

    public Twyerytr(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedRate = 5000)
    public void savePoint() {
        Map<String, String> map = new HashMap<>();
        map.put("car", "car1");
        restTemplate.getForObject("http://172.30.10.129:3011/result/savePoint?car=345&point=qwert", String.class);


//        PointForSaveDto request = new PointForSaveDto("algorithm", "postForEntity", "point", false);
//
//        restTemplate.postForEntity("http://172.30.10.129:3011/result/savePoint", request,String.class);


        System.out.println("=========================================");
//        final ResponseEntity<PointForSaveDto> requestEntity = new ResponseEntity<>(new PointForSaveDto("algorithm", "car", "point", false), HttpStatus.OK);
//        final ResponseEntity<String> exchange = restTemplate.exchange("http://172.30.10.129:3011/result/savePoint", HttpMethod.GET, requestEntity, String.class);

//        final Integer forObject = restTemplate.getForObject(server + savePointUrl, Integer.class, algorithm, savePoint);
    }
}
