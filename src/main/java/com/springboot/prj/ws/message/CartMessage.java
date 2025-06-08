package com.springboot.prj.ws.message;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartMessage extends Message {
    private String cartCode;
    private long finalPrice;
    // private CartItemDTO cartItemDTO;
    private long totalSaleOfPriceByCalc;
    private boolean isAdd;
}
