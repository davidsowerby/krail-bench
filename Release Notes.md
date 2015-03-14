### Release Notes for krail-bench 0.7.12

Minor change to support Krail move to Event Bus

#### Change log

-   [6](https://github.com/davidsowerby/krail-bench/issues/6): Test not closing browsers


#### Dependency changes

   compile dependency version changed to: krail:0.9.0

#### Detail

*Updated version information*


---
*See [krail 346](https://github.com/davidsowerby/krail/issues/346) ViewChangeListeners replaced by Event Bus*


---
*Fix [6](https://github.com/davidsowerby/krail-bench/issues/6) Browsers not closing in test*

Was using the driver variable directly, which does not set the driver up properly for TestBench


---
*See [krail 304](https://github.com/davidsowerby/krail/issues/304) Change to logback*

Logging configuration files changed


---
