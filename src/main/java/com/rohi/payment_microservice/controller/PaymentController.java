package com.rohi.payment_microservice.controller;

import com.rohi.payment_microservice.model.Payment;
import com.rohi.payment_microservice.service.PaymentService;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/*
Resilience4J core modules -
 Circuit Breaker : resilience4j-circuitbreaker
 Retry : resilience4j-retry
 Rate Limiter : resilience4j-ratelimiter
 Bulkhead : resilience4j-bulkhead
 Time Limiter : resilience4j-timelimiter
 Cache : resilience4j-cach
 */

@RestController
@RequestMapping("/payment")
public class PaymentController {

    private final PaymentService service;

    private static Logger log = LoggerFactory.getLogger(PaymentController.class);

    public PaymentController(PaymentService service){
        this.service = service;
    }

    @PostMapping("/pay")
    @CircuitBreaker(name="payment-service",fallbackMethod = "payFallBack")
    public Payment makePayment(@RequestBody Payment payment) throws InterruptedException {

        return service.savePayment(payment);
    }

    public Payment payFallBack(Payment payment, RuntimeException exception){
        payment.setStatus("Failed to process payment. Please try again later");
        payment.setTransactionId(null);
        payment.setId(0);
        payment.setOrderId(0);
        payment.setAmount(0);
        return payment;
    }


    @GetMapping("/{orderId}")
    @TimeLimiter(name = "paymentService", fallbackMethod = "idFallBack")
    public CompletionStage<Payment> findPaymentByOrderId(@PathVariable int orderId) throws Exception {
        return CompletableFuture.supplyAsync(() -> {
//            try {
//                Thread.sleep(5000);
//            } catch (InterruptedException e) {
//                // Handle interruption
//                log.info("Caught Interrupted Exception");
//            }
            return service.findPaymentByOrderId(orderId);
        });
    }

    /*
    the Thread.sleep(5000) is causing the executing thread to sleep for 5 seconds before invoking the
    service.findPaymentByOrderId(orderId) method.

When CompletableFuture.supplyAsync is used with a lambda expression, it executes the provided task asynchronously in a
separate thread managed by a thread pool. The Thread.sleep(5000) simulates a delay of 5 seconds in the asynchronous
task execution before invoking the service.findPaymentByOrderId(orderId) method.

After the 5 seconds of sleep, the service.findPaymentByOrderId(orderId) method will be invoked to fetch the payment
information by the provided orderId. The CompletableFuture will then complete with the result of this method call.

It's important to note that the TimeLimiter is applied to the whole execution of the findPaymentByOrderId method,
including the sleep and the subsequent invocation of service.findPaymentByOrderId(orderId). If the overall execution
time exceeds the configured time limit, the fallback method will be triggered.

The purpose of simulating the delay with Thread.sleep(5000) in this example is to demonstrate the usage of TimeLimiter
 and the fallback method. In a real-world scenario, the delay would typically be caused by an actual time-consuming
  operation, such as making an external API call or performing database operations, rather than a simple sleep.
     */

    public CompletionStage<Payment> idFallBack(int orderId, Throwable throwable) {
        Payment payment = new Payment();
        payment.setStatus("Unable to locate the payment information for the order Id. Please try again later");
        payment.setTransactionId(null);
        payment.setId(0);
        payment.setOrderId(0);
        payment.setAmount(0);
        return CompletableFuture.completedFuture(payment);
    }

    /*
    CompletionStage is an interface in Java that represents a stage of a potentially asynchronous computation, typically
    used for handling asynchronous operations. It provides methods for composing and chaining tasks that are executed
    asynchronously. In this case, the CompletionStage<Payment> represents a future result of the payment retrieval
    operation.

CompletableFuture is an implementation of the CompletionStage interface that provides additional methods for controlling
the completion of a task and handling its results. It allows you to explicitly define and manage asynchronous
computations.

In the code, CompletableFuture.supplyAsync is used to create a new CompletableFuture and supply it with a task to be
executed asynchronously. The task is defined as a lambda expression that sleeps for 5 seconds (simulating a delay) and
 then calls the service.findPaymentByOrderId method to retrieve the payment for the given orderId.

By using CompletableFuture and returning a CompletionStage<Payment>, you are making the findPaymentByOrderId method
 asynchronous. It allows the method to start the payment retrieval operation and return immediately, without blocking
 the execution thread. The actual execution of the task happens asynchronously in a separate thread.

The @TimeLimiter annotation is applied to the method to add a time limit for the execution of the asynchronous task.
If the task exceeds the specified time limit, the idFallBack method will be invoked as a fallback. The TimeLimiter
expects a CompletionStage as the return type because it operates on asynchronous computations and needs to handle
their completion or timeout.

Overall, the use of CompletionStage, CompletableFuture, and @TimeLimiter allows you to handle asynchronous
operations and set a time limit for their execution, providing more control and resilience to your application.

To utilize the TimeLimiter feature effectively, it's recommended to execute the handler method in a separate thread
from the main execution thread. This separation allows the execution to happen asynchronously, so the main thread is
not blocked while waiting for the completion of the potentially time-consuming operation.

By executing the handler method in a separate thread, you ensure that the TimeLimiter can monitor the execution time
and enforce the specified time limit. If the execution exceeds the time limit, the fallback method will be triggered.

In the code example you provided earlier, the CompletableFuture.supplyAsync method is used to execute the task
asynchronously in a separate thread. This allows the TimeLimiter to monitor the execution time and apply the
configured timeout.

Separating the execution into a separate thread allows your application to continue processing other tasks
concurrently, enhancing responsiveness and scalability. It's especially beneficial when dealing with potentially
blocking or long-running operations, such as making network requests or performing heavy computations.

Note that it's important to consider the overall design and resource management when executing tasks in
separate threads. Excessive use of threads or improper thread management can lead to resource contention, thread
 starvation, or other concurrency issues. It's recommended to use thread pools or other concurrency frameworks to
 manage the execution of asynchronous tasks effectively.
     */



}
/*change*/