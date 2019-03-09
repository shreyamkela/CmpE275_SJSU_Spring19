package edu.sjsu.cmpe275.aop;

import java.util.UUID;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args) {
		/***
		 * Following is a dummy implementation of App to demonstrate bean creation with Application context. You may make changes to suit your need, but this file is NOT part of your submission.
		 */

		ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("context.xml");
		SecretService secretService = (SecretService) ctx.getBean("secretService");
		SecretStats stats = (SecretStats) ctx.getBean("secretStats");

		try {
			// XXXXXXXXXXXXXXXXXX INTERMIXED ADVIDES?
			// XXXXXXXXXXXXXXXXXX HOW TO THROW? AND SHOULD CODE RUN AFTER ANY THROW? - DONE THE CHECK FOR PERMANENT NETWORK FAILURE WHEN SHARING A SECRET SO THAT THIS SECRET IS NOT COUNTED AS A SHARED SECRET
			UUID secret = secretService.createSecret(null, "My little secret");
			UUID secret1 = secretService.createSecret("Paul", "My little secret");
			secretService.shareSecret("Alice", secret, "Bob");
			secretService.shareSecret("Paul", secret1, "Bob");
			secretService.readSecret("Bob", secret);
			secretService.unshareSecret("Alice", secret, "Bob");
		} catch (Exception e) {
			e.printStackTrace();
		}

		System.out.println("Best known secret: " + stats.getBestKnownSecret());
		System.out.println("Worst secret keeper: " + stats.getWorstSecretKeeper());
		System.out.println("Most trusted user: " + stats.getMostTrustedUser());
		System.out.println("Longest secret created: " + stats.getLengthOfLongestSecret());
		ctx.close();
	}
}
