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

### Build

1. export `JAVA_HOME` to java 8.

2. `mvn package`. A runnable `*.jar` file should exist in `*/target/`

### Deploy Kubernetes cluster with several server nodes

1. deploy Kubernetes.
2. label each node with `node=XXX`. Replace XXX with the real node id.
3. make sure that the kubectl proxy server is running on the default port.
4. create a namespace named `kube-test` with `kubectl create namespace kube-test.

#### Microservice system for detecting
Adapt the microservice system to the BSF using the MBSTAdapter
The main workflow of the MBSTAdapter is as follows:
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


2. Modify the Controller class make it extend MObject, and add the annotation @MRestApiType to the methods of each Controller class.


3. Add the annotation @Loggable to each method in the @Repository annotated interface.


4. If there is a service invocation using RestTemplate in the method of the service implementation class, add the following code before the service invocation in the method body
   ```java
   headers.remove("Host");
   ```


5. Create a folder named config in the directory where the startup class is located (if it doesn't already exist), and in that folder create LoggableAutoConfiguration.java.

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
   
6. Modify source code, Add @ServletComponentScan("com.septemberhx.common.filter") to the startup class of each microservice.



Then use Maven to install the microservices and then run them on a Kubernetes cluster.

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

The good practices corresponding to these 17 bad smells and their impact are shown in Table 2.

Table 2 Good practices and their impact

| Good practice（Bad smell）    | Impact                                                       |
|-----------------------------| ------------------------------------------------------------ |
| hasAPIVersion(NAV)          | The ability to conduct in-depth analysis based on versions enhances **analysability[1, 2, 3]**.  In the context of multiple microservice versions, the capability to modify or upgrade specific versions according to changing requirements improves **modifiability[1]**.  When multiple versions exhibit different SLA levels, it enhances **fault tolerance**. |
| noCircleDependency(CD)      | Breaking cyclic call chains to avoid forming loops enhances **analysability[1, 2, 3, 4]**. It improves code **reusability[5, 6]** and **modifiability[1, 4]**. With fewer dependencies, modifications in one module require fewer changes in others, boosting **modularity**. The system's capability to handle more business requests increases **adaptability[1]**. Decreasing the length of call chains reduces overall business processing time, enhancing **time behavior**. Reducing information exchange between microservices lowers **interoperability**. |
| noHardcode(HC)              | The absence of fixed addresses enhances **adaptability**. Setting IP and port configurations through environment variables or configuration files improves **analysability**, **reusability**, **modifiability[1]**, and **confidentiality**. |
| noServiceGreedy(SG)         | Each service having a more specific responsibility enhances **modularity[1]**, **analysability[1]**, **reusability**, and **adaptability[1]**. Increased interactions between modules improve **interoperability**. Clearer code localization during modifications enhances **modifiability**. |
| hasAPIGateway(NAG)          | Front-end requests not directly communicating with microservice instances enhances **confidentiality[7]**. However, it may introduce single points of failure, lowering **fault tolerance**. Gateway management of microservice access improves **analysability** and **modifiability[1]**. |
| seperatedDenepency(SD)      | The modification of a dependency library only affecting its corresponding microservice enhances **modifiability[1]**. Increased independence between modules improves **modularity[1, 5, 6]**. When a separate dependency library encounters issues, it does not impact other microservices, enhancing **fault tolerance[1]**. |
| separatedPersistency(SP)    | Each service having its own dedicated database enhances **modularity[1, 5, 6]** and **adaptability**. When data issues arise, they do not affect other services, boosting **fault tolerance[1]**. Modifications to table structures and data only impact the respective service, improving **modifiability[1]**. Individual databases clarify business segmentation, enhancing **analysability**. Services possessing ownership of their private data increase **confidentiality**. Increased data access between modules after eliminating sharing enhances **interoperability**. Absence of multiple services contending for database resources improves **time behavior**. |
| appropriateSvcIntimacy(ISI) | When one service no longer has access to the private data of another service's database, the impact is the same as for separatedPersistency. |
| unitaryStandards(TS)        | Under the same protocol, standards, and frameworks, the code's **analysability** and **modifiability** are enhanced. Data exchange becomes more convenient, improving **interoperability**. |
| correctServiceCut(WC)       | Clearer delineation of responsibilities among modules enhances **modularity**, **analysability[1]**, and **reusability**. Easier to modify, increasing **modifiability[1]**. Simplifies expansion, enhancing **adaptability[1]**. |
| noHUb(Hub)                  | A class having few dependencies on other abstract or concrete classes enhances **analysability** and **modifiability[4]**. |
| noCircleHierarchy(CH)       | The instability of subtypes does not affect the stability of supertypes, enhancing **fault tolerance**. Subtypes do not impact the understanding of supertypes, improving **analysability[8]**. |
| noScatteredFunctionality(SF) | Centralizing functions within each module enhances **modularity[4]**, **reusability**, and **analysability[4]** while reducing **interoperability**. Modifications become more centralized when business logic changes, increasing **modifiability**. Easier expansion improves **adaptability**. |
| noMultipath(MP)             | Clearer hierarchical organization of classes facilitates a better understanding of relationships between classes, enhancing **analysability[8]**. Centralization of code modifications improves **modifiability**. |
| fullUsedAbstract(UA)        | All methods of abstract classes are utilized, enhancing **analysability**. Reducing code redundancy improves **reusability** and **adaptability**. |
| fullUsedInterface(UI)       | All methods of interfaces are utilized, enhancing **analysability**. Reducing code redundancy improves **reusability** and **adaptability**. |
| noESB(ESB)                  | Direct interaction between modules reduces system complexity, enhancing **modularity**, **analysability[1]**, **reusability**, **modifiability[1]**, and **adaptability[1]**. Avoiding single points of failure that can paralyze the entire system improves fault tolerance. Enhanced **interoperability** is achieved by facilitating direct interaction between modules. Absence of ESB-style services as middleware reduces the likelihood of performance bottlenecks, thereby improving **time behavior**. |

The factor of the good smells corresponding to these 17 bad smells and the quality attributes affected are shown in Table 3. ↑ means the good odor can enhance the quality attribute, ↓ means the good odor can reduce the quality attribute. In the quality attributes, T stands for Time behaviour, I stands for Interoperability, F stands for Fault tolerance, C stands for Confidentiality, M stands for Modularity, R stands for Reusability, An stands for Analysability, Mf stands for Modifiability, and Adt stands for Adaptability.

Table 3 Factors of good smells and quality characteristics affected

| Good practice（Bad smell）     | Factor | Quality characteristics affected |
|------------------------------| ------ | -------------------------------- |
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

Table 4  Definitions of the selected quality characteristics in the microservice system

| Subcharacteristics | Description in microservice system                           |
| ------------------ | ------------------------------------------------------------ |
| Time-behavior      | The extent to which each service in a microservices system can quickly respond to user requests and fulfil expected functionalities within a reasonable timeframe. |
| Interoperability   | The degree to which microservices can efficiently exchange information and invoke functionalities through standardized interfaces and protocols. |
| Fault tolerance    | The degree to which the failure of a specific service in a microservices system does not impact the regular operation of the entire system, and services can isolate themselves and self-heal. |
| Modularity         | The degree to which services in a microservices system are highly decoupled, allowing independent deployment and operation, minimizing dependencies and impacts among them. |
| Reusability        | The degree to which services in a microservices system possess generality and reusability, enabling them to be reused by other systems or services. |
| Analyzability      | The degree to which performance metrics, fault information, and other relevant data of each service in a microservices system can be quickly diagnosed and analyzed, facilitating timely issue detection and resolution. |
| Modifiability      | The degree to which modifications of a specific service in a microservices system do not affect other services, allowing independent and rapid modifications. |
| Testability        | The degree to which services in a microservices system can establish effective and efficient testing strategies and standards, conducting tests to verify if the services meet these standards. |
| Adaptability       | The degree to which a microservices system effectively utilizes and adapts to diverse or evolving infrastructure environments (including hardware, operating systems, middleware, etc.) and software environments such as service orchestration and discovery. |

## References

[1] Pulnil S, Senivongse T. A microservices quality model based on microservices anti-patterns[C]//2022 19th International Joint Conference on Computer Science and Software Engineering (JCSSE). IEEE, 2022: 1-6.

[2] Walker A, Das D, Cerny T. Automated code-smell detection in microservices through static analysis: A case study[J]. Applied Sciences, 2020, 10(21): 7800.

[3] Walker A, Das D, Cerny T. Automated microservice code-smell detection[C]//Information Science and Applications: Proceedings of ICISA 2020. Springer Singapore, 2021: 211-221.

[4] Zhong C, Huang H, Zhang H, et al. Impacts, causes, and solutions of architectural smells in microservices: An industrial investigation[J]. Software: Practice and Experience, 2022, 52(12): 2574-2597.

[5] Taibi D, Lenarduzzi V, Pahl C. Microservices anti-patterns: A taxonomy[J]. Microservices: Science and Engineering, 2020: 111-128.

[6] Taibi D, Lenarduzzi V. On the definition of microservice bad smells[J]. IEEE software, 2018, 35(3): 56-62.

[7] Messina A, Rizzo R, Storniolo P, et al. The database-is-the-service pattern for microservice architectures[C]//Information Technology in Bio-and Medical Informatics: 7th International Conference, ITBAM 2016, Porto, Portugal, September 5-8, 2016, Proceedings 7. Springer International Publishing, 2016: 223-233.

[8] Azadi U, Fontana F A, Taibi D. Architectural smells detected by tools: a catalogue proposal[C]//2019 IEEE/ACM International Conference on Technical Debt (TechDebt). IEEE, 2019: 88-97.