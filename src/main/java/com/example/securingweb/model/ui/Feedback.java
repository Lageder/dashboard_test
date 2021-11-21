package com.example.securingweb.model.ui;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Feedback {
    private Double positive;
    private Double negative;
    private Double progress;
    private Integer total;
}
