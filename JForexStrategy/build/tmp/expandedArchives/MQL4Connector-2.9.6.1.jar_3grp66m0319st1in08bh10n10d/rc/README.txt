
=======
General
=======
 
MQL4Connector is an open-source project that provides implementation for Metatrader 4 functions, 
both for strategies and indicators. MQL4Connector does not include .mq4 file conversion to java, 
therefore one has to convert it in JForex Client and then plug-in the library by using 
the @Library annotation, just as using any other include libraries:
http://www.dukascopy.com/wiki/index.php?title=Include_libraries

What concerns running MT4 strategies/indicator in JForex client there are two logical parts:
 - Converter (carries out syntactical conversion between C-like MQL code to java JForex code).
 - Connector (implements MQL functions in JForex)
MQL4Connector replaces the latter. Meaning that if the strategy/indicator does not compile, it
still won't compile by including MQL4Connector as @Library. But if the strategy/indicator does
compile but there are problems/errors at runtime, then changes in MQL4Connector might fix them.

=====
Usage
=====

Assume you have an indicator woodyFix.mq4, in order to use MQL4Connector with it, you have to 
do the following:
1) Open the file in JForex Client.
2) Convert the indicator and change open the converted java file.
3) Change the source file header 
FROM:

package jforex.converted;
import java.awt.Color;
import com.dukascopy.api.*;

public class woodyFix extends ConnectorIndicator {

TO:

package jforex.converted;
import java.awt.Color;
import com.dukascopy.api.*;
import com.dukascopy.connector.engine.*; 
import com.dukascopy.connector.engine.Properties; 
import com.dukascopy.connector.engine.IndicatorBuffer;

@RequiresFullAccess 
@Library("C:/temp/MQL4Connector-2.6.38.1.jar")
public class woodyFix extends MQL4ConnectorIndicator {

4) Attach the indicator to a chart.

========================
Source code architecture
========================

There are two logical parts of the MQL4Connector:
 - Connector engine "wraps" converted MQL strategy/indicator inside a JForex strategy/indicator.
 - Connector library contains all standard MQL4 functions and also Technical Indicators that are 
   supported by JForex. The functions are implemented to behave as close as possible to their 
   equivalents in MQL.
Connector engine and library are loosely coupled via a controller. 

The class hierarchy can be viewed in resources/diagrams.

Functions in connector library are divided in interfaces to match the modules of MQL4. Depending
on a module, there might be common or separate implementations for strategies and indicators.

Since the implementation depends on the JForex-API version, first three numbers of MQL4Connector 
version number match the JForex-API version.

========
Examples
========

Find example strategies in examples\MT4 Strategies and indicators in examples\MT4 indicators






