### Release Notes for krail-bench 0.7.14

Upgrade to Vaadin 7.4.4

#### Change log



#### Dependency changes

   compile dependency version changed to: krail:0.9.2

#### Detail

*Updated version information*


---
*Merge branch 'krail317' into develop*


---
*Fix [krail 317](https://github.com/davidsowerby/krail/issues/317) Guice 4*

The change to Guice 4 highlighted a Tomcat issue with handling a trailing slash - it is not clear why this was an issue for Guice 4, and not Guice 3, but amending tests to handle the trailing slash enabled all regression tests to pass on Tomcat 7 and Tomcat 8


---
*Testapp Tests pass on host tomcat 7*


---
