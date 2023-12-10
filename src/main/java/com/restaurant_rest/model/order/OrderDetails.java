package com.restaurant_rest.model.order;

import com.restaurant_rest.entity.Address;
import com.restaurant_rest.entity.enums.PaymentMethod;
import com.restaurant_rest.model.address.AddressRequest;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class OrderDetails {
    @Valid
    private AddressRequest address;
    @Schema(example = "205")
    private Integer usedBonuses;
    @NotNull(message = "")
    @Enumerated(EnumType.STRING)
    private PaymentMethod payment;
    @Schema(example = "75", description = "Time in minute")
    private Integer deliveryTime;
    @Schema(example = "15:00 - 15:30", description = "Range time [from-to]")
    private String reservedTime;
    @Schema(example = "Comment")
    private String comment;
    @Schema(example = "2", description = "count cutlery")
    private String cutlery;

}
