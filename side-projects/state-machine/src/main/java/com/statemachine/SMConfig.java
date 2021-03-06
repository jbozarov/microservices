package com.statemachine;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Slf4j
@Configuration
@EnableStateMachineFactory
public class SMConfig extends StateMachineConfigurerAdapter<OrderStates, OrderEvents> {

  @Override
  public void configure(StateMachineConfigurationConfigurer<OrderStates, OrderEvents> config)
      throws Exception {

    StateMachineListenerAdapter<OrderStates, OrderEvents> adapter =
        new StateMachineListenerAdapter<OrderStates, OrderEvents>() {
          @Override
          public void stateChanged(
              State<OrderStates, OrderEvents> from, State<OrderStates, OrderEvents> to) {
              //log.debug(" from: " + from.toString() + " and to: " + to.toString());
          }
        };

    config.withConfiguration().autoStartup(false).listener(adapter);
  }

  @Override
  public void configure(StateMachineStateConfigurer<OrderStates, OrderEvents> states)
      throws Exception {
    states
        .withStates()
        .initial(OrderStates.SUBMITTED)
        .state(OrderStates.PAID)
        .end(OrderStates.FULFILLED)
        .end(OrderStates.CANCELLED);
  }

    @Override
    public void configure(StateMachineTransitionConfigurer<OrderStates, OrderEvents> transitions) throws Exception {
        transitions
                .withExternal().source(OrderStates.SUBMITTED).target(OrderStates.PAID).event(OrderEvents.PAY)
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.FULFILLED).event(OrderEvents.FULFILL)
                .and()
                .withExternal().source(OrderStates.SUBMITTED).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL)
                .and()
                .withExternal().source(OrderStates.PAID).target(OrderStates.CANCELLED).event(OrderEvents.CANCEL);

    }
}
