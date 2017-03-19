# SMAC ReadMe

## What we've coded

### Evaluator.java
In this file, we programmed the mainly funcitons such as 'let' processing, 'save' processing and all those key word processing functions. And this file was coded by Fengdingwen(2016229064).

### MathematicalEvaluator.java
We use the class MathematicalEvaluator to evaluate all the mathematical expressions in order to determine whether they are valid expressions, if so, we calculate them, if not, we throw some exceptions. This file was coded by Qixuebin(2016229075) and Dingqixuan(2016229062).

### FunOp.java
In this file, we define a class to represent the operators and delimeters, for example. '+', '-' and '(', ')', etc. And we also defined their priority and number of operands. This file was coded by Fengdingwen(2016229064).

### Result.java 
The class Result was used to decide whether a result is gotten from a calculation of a valid mathematical expression. If so, we should pass it to the global variable 'last'. This file was coded by Qixuebin(2016229075).

### SMACModel.java
In this file we programmed the codes for processing the key word 'log', and if we detect a 'log' in the input string, then we start to write all the following operations into a log file. This file was coded by Dingqixuan(2016229062).