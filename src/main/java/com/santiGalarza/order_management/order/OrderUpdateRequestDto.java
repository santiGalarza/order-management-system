package com.santiGalarza.order_management.order;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OrderUpdateRequestDto {
    private UUID orderStatusId;
    private List<OrderItemUpdateRequestDto> items;
}