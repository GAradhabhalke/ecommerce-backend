package com.company.ecommerce.dtos;

import com.company.ecommerce.entity.OrderStatus;

public class UpdateOrderStatusRequestDto {
    private OrderStatus status;

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }
}
