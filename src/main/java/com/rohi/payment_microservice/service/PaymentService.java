package com.rohi.payment_microservice.service;

import com.rohi.payment_microservice.exception.PaymentFailureException;
import com.rohi.payment_microservice.model.Payment;
import com.rohi.payment_microservice.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.UUID;

@Service
public class PaymentService {

    /*
    Constructor injection is recommended by Spring over field injection for several reasons:

    Explicit Dependencies: Constructor injection makes dependencies explicit. When using constructor injection, you must
    define all the dependencies required by a class as constructor parameters. This makes it clear which dependencies
    are necessary for the class to function properly. With field injection, dependencies can be hidden within the class
    and it may not be immediately apparent which dependencies are required.

    Immutability: Constructor injection promotes immutability. By passing dependencies through the constructor, you can
    make those dependencies final, indicating that they cannot be changed after the object is created. This helps ensure
    that the object's state remains consistent and prevents accidental modification of dependencies.

    Testability: Constructor injection simplifies unit testing. With constructor injection, it's easier to create
    instances of a class for testing purposes. You can pass in mock or stub dependencies directly when constructing the
    object, allowing for more controlled and isolated testing. In contrast, with field injection, you would need to
    rely on additional frameworks or reflection to set the dependencies during testing.

    Clearer Dependency Graph: Constructor injection helps visualize the dependency graph. When using constructor
    injection, the dependencies of a class are explicitly declared in the constructor parameters, making it easier to
    understand the class's dependencies and their relationships. This can help with code maintainability and debugging.

    Avoiding Dependency Cycles: Constructor injection helps avoid dependency cycles. When using field injection, it's
    easier to introduce circular dependencies, where two or more classes depend on each other directly or indirectly.
    Constructor injection forces you to resolve dependencies explicitly, reducing the likelihood of creating circular dependencies.

While field injection via the @Autowired annotation can be convenient in some cases, constructor injection is generally
considered a better practice because it provides clearer, more testable, and more maintainable code. Spring encourages
the use of constructor injection as the default option but also supports other injection types for flexibility.
     */
    private final PaymentRepository repository;

    //constructor injection
    public PaymentService(PaymentRepository repository){
        this.repository = repository;
    }

    /*
    when you annotate a class with the @Service annotation in Spring, it indicates that the class is a candidate for
    being managed as a bean by the Spring container. If you have a constructor marked with @Autowired or with no
    @Autowired annotation at all, Spring will use that constructor to create the bean.

    Note that if you have only one constructor in your class, Spring will automatically consider it as the constructor
    to use for dependency injection, and you don't need to explicitly annotate it with @Autowired. However, if you have
    multiple constructors, you need to indicate which one Spring should use for dependency injection.
     */

    public Payment savePayment(Payment payment) throws PaymentFailureException {

            payment.setStatus(paymentProcessing());
            payment.setTransactionId(UUID.randomUUID().toString());
            return repository.save(payment);


    }

    public String paymentProcessing(){
        //API in real-world scenarios are third party payment gateway
        //such as Paytm, PayPal etc
        return  new Random().nextBoolean()?"success":"failure";
    }


}
