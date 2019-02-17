// For Spring Overview - https://www.tutorialspoint.com/spring/spring_overview.htm
// When writing a complex Java application, application classes should be as independent as possible of other Java classes to increase the possibility to reuse these classes and to test them independently of other classes while unit testing. Dependency Injection helps in gluing these classes together and at the same time keeping them independent.
// For Spring configuration in IDE - https://www.youtube.com/watch?v=lw3fPT8whOc

package com.cmpe275.lab0;

//// The following code is for HelloWorld class without Spring. That is the main will make an object of this class and there is no dependency injection
//
//class HelloWorld implements Greeter {
//	
//	private String name;
//	
//	public void setName(String name) {
//		this.name = name;
//		System.out.println(name); // prints <name>
//	}
//	
//	public String getGreeting() { // the class implements the methods of the interface so the methods have to be public, as the class cannot downgrade the accessiblity
//		System.out.print("Hello " + name);
//		return name;
//	}
//	
////	public static void main(String args[]) {
////		HelloWorld obj = new HelloWorld(); 
////		obj.setName("Shreyam"); // prints <name>
////		String myName = obj.getGreeting(); // prints Hello <name>
////		System.out.print("\nHello " + myName); // prints Hello <name>
////		System.out.print("\nHello " + obj.name); // prints Hello <name>. Here private variable of the class is accessible as main is inside the class itself
////		
////	}
//	
//}
//


public class HelloWorld implements Greeter { // If public not included then it gives "WARNING: Invalid JavaBean property 'name' being accessed! Ambiguous write methods found next to actually used..." warning. But this warning does not any disruption in the program operation and the code runs correctly - https://github.com/spring-projects/spring-framework/issues/17933

	private String name;
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getGreeting() { // the class implements the methods of the interface so the methods have to be public, as the class cannot downgrade the accessiblity 
		return name;
	}

}


