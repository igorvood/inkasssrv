package ru.sberbank.inkass.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReciveCarDto {

    private String car;
    private Long time;
}
