package com.in28minutes.microservices.camelmicroservicea.routes.a;

import java.time.LocalDateTime;

import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

// @Component
public class MyFirstTimerRouter extends RouteBuilder{

    @Autowired
    private GetCurrentTimeBean getCurrentTimeBean;

    @Autowired
    private SimpleLoggingProcessingComponent loggingComponent;

    @Override
    public void configure() throws Exception {

        /** 
         * Below sequence of steps if call route:
         * 
         * queue (First endpoint)       -> listen to queue
         * transformation               -> whatever infromationcomes in want to make some changes
         * database (second endpoint)   -> save it to database
         * 
         * 
         * In this example we will use:
         * timer instead of queue &
         * log instead of database
         * 
        */
        
        /**
         * Ex:1 start point of route
         * 
         * Code: 
         * from("timer:first-timer")
         * .to("log:first-timer");
         * 
         * Output:
         * 2022-01-08 18:34:26.644  INFO 32784 --- [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: null, Body: [Body is null]]
         * 
        */ 
        

        /**
         * Ex: 2
         * 
         * Code:
         * from("timer:first-timer")
         * .transform().constant("My Constant Message")
         * .to("log:first-timer");
         * 
         * Output:
         * 2022-01-08 18:34:25.634  INFO 32784 --- [r://first-timer] first-timer                              : Exchange[ExchangePattern: InOnly, BodyType: String, Body: My Constant Message]
        */


        from("timer:first-timer")
        .log("${body}") // Ex: 7, 8, 9 
        .transform().constant("My Constant Message") // Ex: 2, 7, 8, 9
        .log("${body}") // Ex: 7, 8, 9
        // .transform().constant("Time now is " + LocalDateTime.now()) Ex: 3
        // .bean("getCurrentTimeBean") Ex: 4
        // .bean(getCurrentTimeBean) Ex: 5
        .bean(getCurrentTimeBean, "getCurrentTime") // Ex: 6, 7, 8, 9
        .log("${body}") // Ex: 7, 8, 9
        .bean(loggingComponent) // Ex: 8, 9
        .log("${body}") // Ex: 8, 9
        .process(new SimpleLoggingProcessor()) // Ex: 9
        .to("log:first-timer");
        
    }
    
}


@Component
class GetCurrentTimeBean {
    public String getCurrentTime() {
        return "Time now is " + LocalDateTime.now();
    }

    public Double getCurrentNumber() {
        return Math.random();
    }
}

@Component
class SimpleLoggingProcessingComponent {

    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessingComponent.class);

    public void  process(String message) {
        logger.info("SimpleLoggingProcessingComponent {}", message);
    }
}

@Component
class SimpleLoggingProcessor implements Processor {

    private Logger logger = LoggerFactory.getLogger(SimpleLoggingProcessor.class);

    @Override
    public void process(Exchange exchange) throws Exception {
        logger.info("SimpleLoggingProcessor {}", exchange.getMessage().getBody());
        
    }
    
}