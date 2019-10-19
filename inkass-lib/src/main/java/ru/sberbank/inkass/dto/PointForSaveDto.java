package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PointForSaveDto {

    private String algorithm;
    private String pointName;
}
