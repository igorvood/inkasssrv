package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class EdgeDto {

    private PointDto from;
    private PointDto to;
    private WayInfoDto wayInfo;
}
