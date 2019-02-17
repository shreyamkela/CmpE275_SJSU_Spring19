**CMPE-275** 
- Lab 0 - Hello World Spring Application
- Name - Shreyam Kela
- Student ID - 013775411
- Email ID- shreyamkela@gmail.com

Instructions:-
--------------

1. Create a package named "com.cmpe275.lab0" under src folder in a new lab0 project (Eclipse IDE).

2. Download the Spring Framework and add Spring jar files as a new user library in the project(v5.1.5 used in this project).

3. Create an Interface class "Greeter" and a class "HelloWorld" which implements the interface Greeter. Create the main class with the name "MainApp" which contains the main method Along with these classes and interface we need to create an beans.xml file under src folder.

4. Create the beans.xml under the src folder. The beans.xml contains the configuration for the bean creation.

5. Implement the 2 methods from the Greeter Interface in the HelloWorld class. In the main method, use ApplicationContext to create a greeting bean with the help of the configurations in the beans.xml.

6. In the beans.xml, we configure the bean id "greeter" with the "name" attribute of the HelloWorld class. We specify a name to the name attribute. This property will be injected when the bean is created.

7. Run the project as a java application. 

8. The console output is: "Hello world from Shreyam Kela!".