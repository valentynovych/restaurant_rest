package com.restaurant_rest.model.address;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class AddressResponse {

    @Schema(example = "1")
    private Long id;
    @Schema(example = "Мій дім")
    private String addressName;
    @Schema(example = "Київ")
    private String city;
    @Schema(example = "пр. Коцюбинського")
    private String street;
    @Schema(example = "14")
    private String building;
    @Schema(example = "105")
    private String apartment;
    @Schema(example = "1 під'їзд")
    private String entrance;
    @Schema(example = "1234")
    private String doorCode;
    @Schema(example = "1")
    private String floor;
    @Schema(example = "Коментар")
    private String comment;
}
