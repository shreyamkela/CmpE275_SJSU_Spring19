package edu.sjsu.cmpe275.aop;

//Tests included from git repositories - https://github.com/ruchiagarwalcse/CMPE275-Lab1 , https://github.com/vivek29/Cmpe275-Lab1-AOP-SecretShare

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecretServiceTest {

	// Secret service object
	private SecretService secretService;

	// Test set up
	@Before
	public void setUp() throws Exception {
		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		secretService = context.getBean("secretService", SecretService.class);
	}

	/**
	 * TestA: Bob cannot read Alice’s secret, which has not been shared with Bob.
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result Not authorized exception occurs.
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testA() throws IllegalArgumentException, IOException {
		System.out.println("testA");
		UUID secretId = secretService.createSecret("Alice", "My little Secret");
		secretService.readSecret("Bob", secretId);
	}

	/**
	 * TestB: Alice shares a secret with Bob, and Bob can read it.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Bob can read the secret.
	 */
	@Test
	public void testB() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testB");

		// Alice shares a secret with Bob
		UUID secretId = secretService.createSecret("Alice", "My little Secret");
		secretService.shareSecret("Alice", secretId, "Bob");

		// Bob can read it
		secretService.readSecret("Bob", secretId);
	}

	/**
	 * TestC: Alice shares a secret with Bob, and Bob shares Alice’s secret with Carl, and Carl can read this secret.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * 
	 * @result Carl can read Alice's secret share by Bob.
	 */
	@Test
	public void testC() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testC");

		// Alice shares a secret with Bob
		UUID secretId = secretService.createSecret("Alice", "My little Secret");
		secretService.shareSecret("Alice", secretId, "Bob");

		// Bob shares Alice’s secret with Carl
		secretService.shareSecret("Bob", secretId, "Carl");

		// Carl can read this secret
		secretService.readSecret("Carl", secretId);
	}

	/**
	 * TestD: Alice shares her secret with Bob; Bob shares Carl’s secret with Alice and encounters UnauthorizedException.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Unauthorized exception occurs.
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testD() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testD");

		// Alice shares her secret with Bob
		UUID aliceSecretId = secretService.createSecret("Alice", "My little Secret");
		secretService.shareSecret("Alice", aliceSecretId, "Bob");

		// Bob shares Carl’s secret with Alice and encounters UnauthorizedException
		UUID carlSecretId = secretService.createSecret("Carl", "My little Secret");
		secretService.shareSecret("Bob", carlSecretId, "Alice");
	}

	/**
	 * TestE: Alice shares a secret with Bob, Bob shares it with Carl, Alice unshares it with Carl, and Carl cannot read this secret anymore.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Unauthorized exception occurs.
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testE() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testE");

		// Alice shares a secret with Bob
		UUID aliceSecretId = secretService.createSecret("Alice", "My little Secret");
		secretService.shareSecret("Alice", aliceSecretId, "Bob");

		// Bob shares it with Carl
		secretService.shareSecret("Bob", aliceSecretId, "Carl");

		// Alice unshares it with Carl
		secretService.unshareSecret("Alice", aliceSecretId, "Carl");

		// Carl cannot read this secret anymore
		secretService.readSecret("Carl", aliceSecretId);
	}
}