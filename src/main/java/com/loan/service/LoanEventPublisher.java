package com.loan.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import com.loan.ApplicationSubmitEvent;

/**
 * Publishes loan submit event
 */
@Service
public class LoanEventPublisher {

  private static final Logger log = LoggerFactory.getLogger(LoanEventPublisher.class);
  private final StreamBridge streamBridge;

  public LoanEventPublisher(StreamBridge streamBridge) {
    this.streamBridge = streamBridge;
  }

  public void publish(ApplicationSubmitEvent event) {
    log.info("Publishing ApplicationSubmitEvent to topic: loans/originationservices/application/submitted/>  id={}", event.getEventId());
 
    String topic = String.format(
    	    "loans/originationservices/application/submitted/%s/%s",
    	    event.getLoanApplication().getLoanId(), event.getLoanApplication().getApplicant().getApplicantId());

    streamBridge.send(topic, event);

  }
  
  
}
