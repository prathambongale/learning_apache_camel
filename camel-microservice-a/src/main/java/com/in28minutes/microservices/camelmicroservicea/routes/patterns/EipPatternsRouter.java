package com.in28minutes.microservices.camelmicroservicea.routes.patterns;

import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.model.dataformat.JsonLibrary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

import com.in28minutes.microservices.camelmicroservicea.CurrencyExchange;

import org.apache.camel.AggregationStrategy;
import org.apache.camel.Exchange;

@Component
public class EipPatternsRouter extends RouteBuilder{

    @Override
    public void configure() throws Exception {
        
        /*from("timer:multicast?period=1000")
        .multicast()
        .to("log:something1", "log:something2");*/
        
        /*from("file:files/csv")
        .unmarshal()
        .csv()
        .split(body())
        .to("log:split-files");*/

        from("file:files/aggregate-json")
        .unmarshal()
        .json(JsonLibrary.Jackson, CurrencyExchange.class)
        .aggregate(simple("${body.to}"), new ArrayListAggregationStartegy())
        .completionSize(3)
        //.completionTimeout(HIGHEST)
        .to("log:aggregate-json"); 

        String routingSlip = "direct:endpoint1, direct:endpoint3";
        // String routingSlip = "direct:endpoint1, direct:endpoint2, direct:endpoint3";

        from("timer:routingSlip?period=10000")
		.transform()
        .constant("My Message is Hardcoded")
		.routingSlip(simple(routingSlip));

        from("direct:endpoint1")
        .to("log:directendpoint1");

        from("direct:endpoint2")
        .to("log:directendpoint2");

        from("direct:endpoint3")
        .to("log:directendpoint3");
    }
    
}

class ArrayListAggregationStartegy implements AggregationStrategy {

    @Override
    public Exchange aggregate(Exchange oldExchange, Exchange newExchange) {
        Object newBody = newExchange.getIn().getBody();
        ArrayList<Object> list = null;
        if (oldExchange == null) {
            list = new ArrayList<Object>();
            list.add(newBody);
            newExchange.getIn().setBody(list);
            return newExchange;
        } else {
            list = oldExchange.getIn().getBody(ArrayList.class);
            list.add(newBody);
            return oldExchange;
        }
    }

}