package com.rohi.payment_microservice.controller;

import com.rohi.payment_microservice.model.Payment;
import com.rohi.payment_microservice.service.PaymentService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService service;

    public PaymentController(PaymentService service){
        this.service = service;
    }

    @PostMapping("/pay")
    public Payment makePayment(@RequestBody Payment payment){
        return service.savePayment(payment);
    }


}
