package com.example.securingweb.model.ui.daily;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class GuestGraph {
    
    private String label;

    private String value;

    public static GuestGraph from() {
        return GuestGraph.builder()
            .build();
    }
}
