Leightweigth Camel CDI Integration
==================================

Camel supports since version 2.10 CDI with [camel-cdi](http://camel.apache.org/cdi.html).
This implementation relies on [deltaspike](http://incubator.apache.org/deltaspike) and uses a common pattern (Singleton/Startup) to start an CamelCDIContext (I also have a few ideas from this project used, thank you).

But here, the main actor is a CDI-Extension:   

- creates the CamelContext
- integrates the CDI-BeanManager with the CamelContext for bean discovery
- discovers and registers all available Java DSL routes  
- discovers and registers all beans with camel annotations: @Consume, @Produce  
- starts the context

That's all. Nothing more to do ...

How to  

- create a consumer ? Write a bean and use @Consume (see [SampleFileConsumer](https://github.com/dstraub/cci/blob/master/cci-impl/src/test/java/de/ctrlaltdel/cci/sample/SampleFileConsumer.java)).
- create a producer ? Write a bean and use @Produce (see [SampleJmsProducer](https://github.com/dstraub/cci/blob/master/cci-impl/src/test/java/de/ctrlaltdel/cci/sample/SampleJmsProducer.java)).
- create a route ? Write a bean an implement RouteBuilder (see [SampleJmsRoute](https://github.com/dstraub/cci/blob/master/cci-impl/src/test/java/de/ctrlaltdel/cci/sample/SampleJmsRoute.java)).
- create a componet ? Write a bean/component and use @Named (see [JmsComponent](https://github.com/dstraub/cci/blob/master/cci-impl/src/test/java/de/ctrlaltdel/cci/sample/JmsComponent.java) / [HelloWorldComponent](https://github.com/dstraub/cci/blob/master/cci-impl/src/test/java/de/ctrlaltdel/cci/sample/comp/HelloWorldComponent.java)). 
- access the CamelContext ? Use @Inject (see [CciTestService](https://github.com/dstraub/cci/blob/master/cci-testapp/src/main/java/de/ctrlaltdel/cci/testapp/CciTestService.java)).

The rest is done by CDI magic. Run [TestCamelContext](https://github.com/dstraub/cci/blob/master/cci-impl/src/test/java/de/ctrlaltdel/cci/TestCamelContext.java) ...

I have only used Weld and JBoss-AS 7 for development and tests.

Projects:
--------  
  
Build the project as usual with 'mvn install'.  

Run maven with the profile 'local' and parameter 'jboss.as.home' copies a test application and camel modules in the corresponding JBoss directories :   
`mvn install -Plocal -Djboss.as.home=JBOSS_AS_HOME_PATH`

- cci-impl:  
  Implementation of a litte bit CDI magic, JUnit tests using Weld
  
- cci-module:   
  JBoss 7 module with some required camel components.   
  Unzip the artefact in the jboss-as-xxx/modules directory.
  
  
- cci-testapp:  
  Simple REST-application for test purpose, copy the artefact to jboss-as-xxx/standalone/deployments directory.  
  The JMS sample requires an running activemq-broker on the same host (with default port 61616).
  
  Test the application with 
  - curl localhost:8080/cci - list the injected CamelContext with routes and endpoints
  - curl localhost:8080/cci/jms/hallo - routes "hallo" to the topic 'data'
  - curl localhost:8080/cci/file/hallo - routes "hallo" to the jboss-as-xxx/standalone/tmp/end     
 


 
 
