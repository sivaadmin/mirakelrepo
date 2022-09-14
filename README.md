## mp-mirakl-orch

Orchestrator between Mirakl integration layer & EPC Applications

Need to set the environment variable GOOGLE_APPLICATION_CREDENTIALS pointing to service account key
GOOGLE_APPLICATION_CREDENTIALS=serviceaccountkey.json 
 


To build the application in local, use

mvn clean package

To build and run the application on server

mvn clean package

java -Dspring.config.location=application.properties -jar ./target/mp-mirakl-orch.jar

To Access Actuator call the following URL
1) http://localhost:8080/mp-mirakl-orch/actuator/health

   Expected success response
   {"status":"UP"}
2) http://localhost:8080/mp-mirakl-orch/actuator/info

   Expected success response
   {"git":{"commit":{"id":"ab128ae"}},"build":{"artifact":"mp-mirakl-orch","name":"mp-mirakl-orch","time":"2022-06-27T07:20:26.211Z","version":"22.1.0-SNAPSHOT","group":"com.macys.mirakl"}}


