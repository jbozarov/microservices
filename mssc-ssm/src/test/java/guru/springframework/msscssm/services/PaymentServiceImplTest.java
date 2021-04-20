package guru.springframework.msscssm.services;

import guru.springframework.msscssm.domain.Payment;
import guru.springframework.msscssm.domain.PaymentEvent;
import guru.springframework.msscssm.domain.PaymentState;
import guru.springframework.msscssm.repository.PaymentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.statemachine.StateMachine;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PaymentServiceImplTest {

  @Autowired PaymentService paymentService;

  @Autowired PaymentRepository paymentRepository;

  Payment payment;

  @BeforeEach
  void setUp() {
    payment = Payment.builder().amount(new BigDecimal("12.99")).build();
  }

  @Transactional
  @Test
  void preAuth() {
    Payment savedPayment = paymentService.newPayment(payment);

    System.out.println("line 37 " + savedPayment.getState().name());

    StateMachine<PaymentState, PaymentEvent> sm = paymentService.preAuth(savedPayment.getId());

    System.out.println("line 40 " + savedPayment.getState().name());

    System.out.println("line 39 " + sm.getState().getId());

    Payment preAuthedPayment = paymentRepository.getOne(savedPayment.getId());

    System.out.println("line 43 " + sm.getState().getId());

    System.out.println(preAuthedPayment);
  }

  @Transactional
  @RepeatedTest(10)
  void testAuth() {
    Payment savedPayment = paymentService.newPayment(payment);

    StateMachine<PaymentState, PaymentEvent> preAuthSM = paymentService.preAuth(savedPayment.getId());

    if (preAuthSM.getState().getId() == PaymentState.PRE_AUTH) {
      System.out.println("Payment is Pre Authorized");

      StateMachine<PaymentState, PaymentEvent> authSM = paymentService.authorizePayment(savedPayment.getId());

      System.out.println("Result of Auth: " + authSM.getState().getId());
    } else {
      System.out.println("Payment failed pre-auth...");
    }
  }
}