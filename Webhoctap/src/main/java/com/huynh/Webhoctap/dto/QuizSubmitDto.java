package com.huynh.Webhoctap.dto;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class QuizSubmitDto {

    // key = maCauHoi, value = maLuaChon student chọn
    private Map<Integer, Integer> dapAn = new HashMap<>();
}
