package com.in28minutes.microservices.camelmicroservicea.routes.c;

import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
public class ActivateMqSenderRouter extends RouteBuilder {

    @Override
    public void configure() throws Exception {
        
        /** Example 1: sending timer on ActiveMq
        
        from("timer:active-mq-timer?period=1000")
        .transform()
        .constant("My message for Activate MQ")
        .log("${body}")
        .to("activemq:my-activemq-queue");*/

        /** 
        from("file:files/json")
        .log("${body}")
        .to("activemq:my-activemq-queue");
        */

        from("file:files/xml")
        .log("${body}")
        .to("activemq:my-activemq-xml-queue");

    }
    
}
