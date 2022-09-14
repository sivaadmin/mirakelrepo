package com.macys.mirakl.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublishUpcData {
    private String upc;
    private String productSku;
    private String opDiv;
    private boolean isImageUpc;
}