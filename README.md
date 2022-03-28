# EXQuery

*GitHub code repository for the EXQuery project - [http://www.exquery.org](http://www.exquery.org)*

[![Test](https://github.com/exquery/exquery/actions/workflows/ci.yml/badge.svg)](https://github.com/exquery/exquery/actions/workflows/ci.yml)

The main thing here at the moment apart from simple abstractions for dealing with XQuery 1.0 and XQuery 3.0 concepts, is the [RESTXQ](http://exquery.github.com/exquery/exquery-restxq-specification/restxq-1.0-specification.html) Project.
The original paper [RESTful XQuery: Standardised XQuery 3.0 Annotations for REST](http://www.adamretter.org.uk/papers/restful-xquery_january-2012.pdf) is available, as is a simple [presentation on RESTXQ](http://www.adamretter.org.uk/presentations/restxq_mugl_20120308.pdf).

The language is Java 6 (also tested against Oracle JDK 7 and OpenJDK 7) and the build system is Maven 3.
It is planned that a C++ implementation will also follow eventually.

## Build
1. Clone the Repository
```bash
git clone https://github.com/exquery/exquery.git
```

2. Enter the cloned repo
```bash
cd exquery
```

3. mvn clean install

4. Each Module will contain a .jar file in its target/ folder

## Modules
EXQuery modules have no external dependencies (currently).

### exquery-restxq-specification
This is the specification for RESTXQ 1.0, written in [ReSpec](http://github.com/darobin/respec).
The latest publicly available and viewable specification is available [here](http://exquery.github.com/exquery/exquery-restxq-specification/restxq-1.0-specification.html) via. our gh-pages branch. 

### exquery-restxq-api
API for the EXQuery RESTXQ project.

### exquery-restxq	
Base Implementation of EXQuery RESTXQ project, builds on the [exquery-restxq-api](#exquery-restxq-api).

### exquery-common
Common Interfaces and abstractions needed for any EXQuery project.

### exquery-xquery
Abstractions for XQuery 1.0.

### exquery-xquery3
Abstractions for XQuery 3.0.

### exquery-annotations-common-api
API Abstractions for any EXQuery Annotations project.

### exquery-annotations-common
Base Implementation for any EXQuery Annotations project, builds on the [exquery-annotations-common-api](#exquery-annotations-common-api).

### exquery-serialization-annotations-api
API for EXQuery Annotations based on the [W3C XSLT and XQuery Serialization 3.0 specification](http://www.w3.org/TR/xslt-xquery-serialization-30/)

### exquery-serialization-annotations
Base Implementation of EXQuery Annotations based on [W3C XSLT and XQuery Serialization 3.0 specification](http://www.w3.org/TR/xslt-xquery-serialization-30/), builds on the [exquery-serialization-annotations-api](#exquery-serialization-annotations-api).

### exquery-parent
Maven parent POM for common Maven settings for each module.
