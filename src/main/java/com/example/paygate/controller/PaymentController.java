package com.example.paygate.controller;

import com.example.paygate.model.Payment;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import util.Util;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;



@Controller
@RequestMapping(value = "/transaction")
public class PaymentController {

    static  final String INITIATE_URL = "https://secure.paygate.co.za/payweb3/initiate.trans";

    static  final String REDIRECT_URL = "https://secure.paygate.co.za/payweb3/process.trans";

    @GetMapping(value = "/checkout")
    public String getCheckOut() {
        return "checkout";
    }

    @PostMapping(value = "/notify",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String  getNotifyPayment(@RequestBody MultiValueMap<String, String> notification,Model model) {
        model.addAttribute("text",notification.toString());
        return "notify";
    }

    @PostMapping(value = "/return", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public String getPaymentReturnData(@RequestBody MultiValueMap<String, String> paymentResponseMap,Model model) {
        model.addAttribute("text",paymentResponseMap.toString());
        return "success";
    }

    @PostMapping(value = "/process")
    public String getPay(Model model) {

        String payGateId = "10011072130";
        String reference = "RC-1234";
        String amount = "10000000";
        String currency = "ZAR";
        String notifyUrl = "https://phakamanicreche.azurewebsites.net/transaction/notify";
        String returnUrl = "https://phakamanicreche.azurewebsites.net/transaction/return";
        String transactionDate = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        String locale = "en-za";
        String country = "ZAF";
        String email = "simphiwe@gmail.com";

        String formFields = payGateId + reference + amount + currency
                + returnUrl  + transactionDate + locale + country + email + notifyUrl;

        String checksum = Util.createCheckSum(formFields ,"secret");

        WebClient webClient = WebClient.create();

        Mono<String> response = webClient.post()
                .uri(INITIATE_URL)
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters
                        .fromMultipartData("PAYGATE_ID", payGateId)
                        .with("REFERENCE", reference)
                        .with("AMOUNT", amount)
                        .with("CURRENCY", currency)
                        .with("RETURN_URL", returnUrl)
                        .with("TRANSACTION_DATE", transactionDate)
                        .with("LOCALE", locale)
                        .with("COUNTRY", country)
                        .with("EMAIL", email)
                        .with("NOTIFY_URL",notifyUrl)
                        .with("CHECKSUM", checksum)
                )
                .retrieve()
                .bodyToMono(String.class);

        String responseBody = response.block();

        final  var data = Util.extractData(responseBody);

        Payment payment = Payment.builder()
                .payGateId(data.get("PAYGATE_ID"))
                .payRequestId(data.get("PAY_REQUEST_ID"))
                .reference(data.get("REFERENCE"))
                .checksum(data.get("CHECKSUM"))
                .url(REDIRECT_URL)
                .build();
        model.addAttribute("transaction",payment);

        return  "payment";
    }

}

