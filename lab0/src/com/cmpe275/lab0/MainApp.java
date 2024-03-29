package com.cmpe275.lab0;

// Add Spring Jar Files to the project
/*
For a Java Project then create user library with all jar files and add that library as a user library
to the project. For a Dynamic Web Project add all these jar files from libs folder of spring framework zip
to project in Eclipse. We need to copy these jar files to lib folder which is found under web Content/WEB-INF.
*/
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainApp {

////The following code is for MainApp without Spring. That is the main will make an object of the HelloWorld class and there is no dependency injection
//
//	public static void main(String[] args) {
//		HelloWorld obj = new HelloWorld(); 
//		obj.setName("Shreyam"); // prints <name>
//		String myName = obj.getGreeting(); // prints Hello <name>
//		System.out.print("\nHello " + myName); // prints Hello <name>
////		System.out.print("\nHello " + obj.name); // This doesnt work in MainApp as here private variable of the class HellowWorld is not accessible as main is inside a different class
//
//	}

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
		HelloWorld obj = (HelloWorld) context.getBean("greeter");
		String name = obj.getGreeting();
		System.out.print("Hello world from " + name + "!");
		
		((ClassPathXmlApplicationContext) context).close(); // If the context is not closed, it gives resource lean error - https://stackoverflow.com/questions/14184059/spring-applicationcontext-resource-leak-context-is-never-closed
	}
	
}
