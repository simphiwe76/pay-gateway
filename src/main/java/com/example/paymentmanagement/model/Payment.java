package com.example.paymentmanagement.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    private String url;
    private String payGateId;
    private String payRequestId;
    private String reference;
    private String checksum;
}
