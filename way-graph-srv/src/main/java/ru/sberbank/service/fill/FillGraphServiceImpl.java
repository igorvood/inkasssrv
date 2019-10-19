package ru.sberbank.service.fill;


import org.springframework.stereotype.Service;
import ru.sberbank.inkass.dto.EdgeDto;
import ru.sberbank.inkass.dto.GraphDto;
import ru.sberbank.inkass.dto.PointDto;
import ru.sberbank.inkass.dto.WayInfoDto;
import ru.sberbank.inkass.property.StartPropertyDto;

import java.util.ArrayList;
import java.util.List;

import static ru.sberbank.inkass.dto.TypePoint.*;


@Service
public class FillGraphServiceImpl implements FillGraphService {

    private final StartPropertyDto propertyDto;

    public FillGraphServiceImpl(StartPropertyDto propertyDto) {
        this.propertyDto = propertyDto;
    }

    public GraphDto fill(int size) {

        List<PointDto> pointDtos = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            double sum = i == 0 ? 0 : Math.random() * propertyDto.getMaxSumInPoint();
//            sum = i == 1 ? 100_000_000 : sum;
            pointDtos.add(PointDto.builder()
                    .typePoint(i == 0 ? GARAGE : i == 1 ? BANK : INKASS_POINT)
//                    .isBase(i == 0)
                    .name(i == 0 ? GARAGE.name() : i == 1 ? BANK.name() : String.format("Point %d", i))
                    .sum(sum)
                    .timeInPoint(Math.random() * propertyDto.getMaxTimeInPoint())
                    .build());
        }


        GraphDto wayInfoDtoMap = new GraphDto();
        final List<EdgeDto> edgeDtos = wayInfoDtoMap.getEdgeDtos();
        for (int i = 0; i < size; i++) {
            for (int j = i + 1; j < size; j++) {
                final WayInfoDto wayInfoDto = (i == 0 && j == 1) ? new WayInfoDto(1, 1L) : new WayInfoDto(Math.random() * propertyDto.getMaxTimeInWay(), 1L);
                edgeDtos.add(new EdgeDto(pointDtos.get(i), pointDtos.get(j), wayInfoDto));
                edgeDtos.add(new EdgeDto(pointDtos.get(j), pointDtos.get(i), wayInfoDto));
            }
        }

        return wayInfoDtoMap;
    }
}
