# MBST：Microservice Bad smells Testbed

## Components：

- **Bad Smells Framework (BSF)**: This framework mainly implements microservice call chain path tracing and database access tracing. The microservice system adapted to this framework generates data in the runtime phase and stores it persistently through directory mounts for runtime analysis. It consists of two parts: common and MClient.

- **Static Analysis**: This component pulls the source code and performs static code analysis locally to provide preliminary results of MBS detection.

- **Bad Smells Server (BSServer)**: This component first obtains the preliminary results, then collects the runtime data through Elasticsearch for final MBS detection, and obtains the system health status H through the assessment models.

- **Build Center**: Builds a Jenkins workflow based on the received repository address and instructions, and then sends the workflow to the Jenkins server.

- **Runtime Information Collector(MInfoCollector)**: Responsible for collecting log information generated during the runtime phase, and sending the collected information to Logstash.

- **Performance Information Collector(MContainerMetricsCollector)**: Collects the CPU and RAM usage of microservice instances by calling the Kubernetes API server interface.

- **User Request**: This component is used to simulate user requests, formulate a request queue based on business logic and send it.

- **Analysis & Display**: This component pulls the source code locally and pushes the microservices source code adapted to the BSF to the gitee repository. H is obtained through BSServer for visual presentation and management of detection results.

  Analysis & Display component is under development and will be presented later in the form of a publicly accessible platform.

## The process of deploy：

#### Build

1. export `JAVA_HOME` to java 8.

2. `mvn package`. A runnable `*.jar` file should exist in `*/target/`

### Deploy Kubernetes cluster with several server nodes

1. deploy Kubernetes.
2. label each node with `node=XXX`. Replace XXX with the real node id.
3. make sure that the kubectl proxy server is running on the default port.
4. create a namespace named `kube-test` with `kubectl create namespace kube-test.

#### Microservice system for detecting

1. Add dependencies in pom.xml

   ```java
           <dependency>
               <groupId>MBS</groupId>
               <artifactId>MClient</artifactId>
               <version>1.0.1</version>
               <scope>compile</scope>
           </dependency>
           <dependency>
               <groupId>MBS</groupId>
               <artifactId>common</artifactId>
               <version>1.0-SNAPSHOT</version>
               <scope>compile</scope>
           </dependency>
           <dependency>
               <groupId>org.springframework.boot</groupId>
               <artifactId>spring-boot-configuration-processor</artifactId>
               <optional>true</optional>
           </dependency>
   ```



2. Create a folder named config in the directory where the startup class is located (if it doesn't already exist), and in that folder create LoggableAutoConfiguration.java.

   ```java
    import com.mbs.mclient.aspect.LoggingAspect;
    import org.springframework.context.annotation.Bean;
    import org.springframework.context.annotation.Configuration;
    import org.springframework.context.annotation.EnableAspectJAutoProxy;

    @Configuration
    @EnableAspectJAutoProxy
    public class LoggableAutoConfiguration {

   
        @Bean
        public LoggingAspect loggingAspect() {
            return new LoggingAspect();
        }
    }
   ```



3. Modify source code，Add @ServletComponentScan("com.septemberhx.common.filter") to the startup class of each microservice.

4. Modify the Controller class make it extend MObject, and add the annotation @MRestApiType to the methods of each Controller class.

5. Add the annotation @Loggable to each method in the @Repository annotated interface.

6. When we make a service call using RestTemplate, we should make the following adjustments if we use the headers we received earlier:

   ```java
   headers.remove("Content-Type");
   headers.remove("Host");
   ```

   and then call the RestTemplate class method to complete the service call.

7. maven install microservices and run them on Kubernetes cluster.

#### Static Analysis

Make it run on node that are not part of the Kubernetes cluster.

#### Bad Smells Server (BSServer)

modify the code about call API of Static Analysis，and run it on node that are not part of the Kubernetes cluster.

#### Collector：MInfoCollector、MContainerMetricsCollector

Run a  MInfoCollector on each worker node，run MContainerMetricsCollector in one worker node.

#### User Request

The requests can be given through real use of deployed microservices, or through automated generation of instructions from component.

------

The equations for calculating the coverage rate of 17 bad smells are shown in Table 1.

Table 1 Coverage calculation formula

| Bad smell                      | Coverage calculation equation                                |
| ------------------------------ | ------------------------------------------------------------ |
| API Versioning                 | C=number of APIs are not semantically versioned / total number of system APIs |
| Cyclic Dependency              | C=number of microservices with cyclic dependency / total number of microservices |
| Hard-coded Endpoints           | C=number of Java files with hard-coded endpoints / total number of Java files |
| Service Greedy                 | C=number of microservices with service greedy / total number of microservices |
| Not Having an API Gateway      | C=1, exists; 0, does not exist                               |
| Shared Libraries               | C=number of microservices with shared libraries / total number of microservices |
| Shared Persistency             | C=number of microservices with shared persistency / total number of microservices |
| Inappropriate Service Intimacy | C=number of microservices with inappropriate service intimacy / total number of microservices |
| Too Many Standards             | C=0, only Java; the percentage of languages other than java, the system includes multiple languages including Java |
| Wrong Cuts                     | C=number of microservices with wrong cuts / total number of microservices |
| Hub-Like Dependency            | C=number of microservices of classes and interfaces with hub-like dependency / total number of microservices |
| Cyclic Hierarchy               | C=number of microservices of classes and interfaces with cyclic hierarchy / total number of microservices |
| Scattered Functionality        | C=number of microservices with scattered functionality / total number of microservices |
| Multipath Hierarchy            | C=number of microservices of classes and interfaces with multipath hierarchy / total number of microservices |
| Underused Interface            | C = number of underused interfaces / total number of interfaces |
| Underused Abstract             | C = number of underused abstract classes / total number of abstract classes |
| ESB Usage                      | C=1, exists; 0, does not exist                               |

The factor of the good smells corresponding to these 17 bad smell and the quality attributes affected are shown in Table 2. ↑ means the good odor can enhance the quality attribute, ↓ means the good odor can reduce the quality attribute. In the quality attributes, T stands for Time behaviour, I stands for Interoperability, F stands for Fault tolerance, C stands for Confidentiality, M stands for Modularity, R stands for Reusability, An stands for Analysability, Mf stands for Modifiability, and Adt stands for Adaptability.

Table 2 Factors of good smells and quality characteristics affected

| Good smell（Bad smell）      | Factor | Quality characteristics affected |
| ---------------------------- | ------ | -------------------------------- |
| hasAPIVersion(NAV)           | 3      | ↑: An,Mf,F                       |
| noCircleDependency(CD)       | 7      | ↑: An,R,Mf,M,Adt,T ↓: I          |
| noHardcode(HC)               | 5      | ↑: Adt,An,R,Mf,C                 |
| noServiceGreedy(SG)          | 6      | ↑: M,An,R,Adt,I,Mf               |
| hasAPIGateway(NAG)           | 4      | ↑: C,An,Mf ↓: F                  |
| seperatedDenepency(SD)       | 3      | ↑: Mf,M,F                        |
| separatedDatabase(SDB)       | 8      | ↑: M,Adt,F,Mf,An,C,I,T           |
| appropriateSvcIntimacy(USI)  | 8      | ↑: M,Adt,F,Mf,An,C,I,T           |
| unitaryStandards(TS)         | 3      | ↑: An,Mf,I                       |
| correctServiceCut(WSC)       | 5      | ↑: M,An,R,Mf,Adt                 |
| noHUb(Hub)                   | 2      | ↑: An,Mf                         |
| noCircleReference(CR)        | 2      | ↑: F,An                          |
| noScatteredFunctionality(SF) | 6      | ↑: M,R,An,Mf,Adt ↓: I            |
| noMultipath(MP)              | 2      | ↑: An,Mf                         |
| fullUsedAbstract(UA)         | 3      | ↑: An,R,Adt                      |
| fullUsedInterface(UI)        | 3      | ↑: An,R,Adt                      |
| noESB(ESB)                   | 8      | ↑: M,An,R,Mf,Adt,F,I,T           |

