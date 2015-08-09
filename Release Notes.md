### Release Notes for krail-bench 0.8.1

This version fixes SubPagePanelPageObject

#### Change log



#### Dependency changes

   compile dependency version changed to: krail:0.9.6

#### Detail

*Pre-release - update version descriptions and version properties*


---
*SubPagePanelPageObject revised*

The previous method for returning the visible sub-pages fails.  Cause is not certian, but could be the change of theme - the old method relied on the getText() method of the panel.

 Revised to actually look for the navigation buttons


---
