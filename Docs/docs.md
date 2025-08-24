## MSSE 672 - Documentation Overview

This document provides a summary of each week’s programming objectives and deliverables for the MSSE 672 course. Each module introduced a core concept in modern Java enterprise development and required a functional implementation within the Triangle Middleware Application.

---

### Week 1 - Eclipse and Log4j

**Objective:** Import Software Development Project and instrument it using Log4J.
**Assignment:** Import the project into Eclipse (or equivalent IDE). Add Log4J-based logging throughout the application to track key actions and behaviors.

---

### Week 2 - Collections and Generics

**Objective:** Use Generic Collections for managing collections of data objects.
**Assignment:** Enhance the application to manage Quadrilateral objects using Java Generics and Collection classes (e.g., List, Map). Include proper logging and unit tests.

---

### Week 3 - JDBC

**Objective:** Use JDBC to store and retrieve data objects in a RDBMS.
**Assignment:** Implement a JDBC-based service for persisting and querying Quadrilateral records. Include logging and integration tests.

---

### Week 4 - ORM

**Objective:** Use Hibernate to store and retrieve data objects in a RDBMS.
**Assignment:** Integrate Hibernate ORM and refactor JDBC logic to use annotated entities and Spring Data repositories where appropriate. Validate CRUD operations through tests.

---

### Week 5 - XML

**Objective:** Use the SAX and DOM Java API functionality to parse XML files.
**Assignment:** Convert the ServiceFactory configuration into an XML format. Use either SAX or DOM API to parse the XML and instantiate services. The XML logic was not wired into the runtime but demonstrated understanding of parsing techniques.

---

### Week 6 - Networking and Sockets

**Objective:** Use network sockets to implement Client/Server programming.
**Assignment:** Implement a raw socket-based authentication server. The server validates username and password credentials before allowing access to the application.

---

### Week 7 - Threads

**Objective:** Use the Java API to implement threaded programming.
**Assignment:** Refactor the authentication server to support multithreading using an ExecutorService. Multiple client connections can now be handled concurrently.

---

### Week 8 - Spring IoC and Dependency Injection

**Objective:** Use the Spring framework to implement dependency injection.
**Assignment:** Replace ServiceFactory logic with Spring-based dependency injection. Refactor service layer to be managed by Spring with annotations or XML configuration. Leverage proper bean registration and interface-based programming.

---

### Notes

* All assignments are located in the `/src/main/java/org.msse672.geometryApp` structure.
* Each week's work builds on the previous, culminating in a modular and testable enterprise-style application.
* Test classes are located in `src/test/java` and cover both unit and integration testing using JUnit 5.

Refer back to this document for a high-level summary of course deliverables and implementation scope.
