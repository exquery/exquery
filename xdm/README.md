API for XQuery 1.0 and XPath 2.0 Data Model (XDM) (Second Edition)
==================================================================

This is a simple model API for XDM. The goal is to form the basis
of the data model interfaces that can be used in various EXQuery module
functions.

In this way, EXQuery modules can define operations that consume
or produce XDM types. A vendor of an XQuery (or perhaps even XPath, XSLT or XProc)
processor can then by simply implementing this data model, gain access to many pre-built
XQuery functions.