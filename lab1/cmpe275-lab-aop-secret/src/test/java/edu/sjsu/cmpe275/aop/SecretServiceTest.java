package edu.sjsu.cmpe275.aop;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

//Tests included from git repositories - https://github.com/ruchiagarwalcse/CMPE275-Lab1

import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SecretServiceTest {

	// Secret service object
	private SecretService secretService;
	// Secret stats object
	private SecretStats stats;

	// Test set up
	@Before
	public void setUp() throws Exception {

		ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("context.xml");
		secretService = context.getBean("secretService", SecretService.class);
		stats = context.getBean("secretStats", SecretStats.class);
		// context.close(); // Dont close context here are resetStats doesnt work while testing if we close the context - resetStats does not clear the objects if context is closed

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

	/**
	 * TestF: Alice shares a secret with Bob and Carl; Carl shares it with Bob, then Alice unshares it with Bob; Bob cannot read this secret anymore.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Unauthorized exception occurs.
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testF() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testF");

		// Alice shares a secret with Bob and Carl
		UUID aliceSecretId = secretService.createSecret("Alice", "My little secret");
		secretService.shareSecret("Alice", aliceSecretId, "Bob");
		secretService.shareSecret("Alice", aliceSecretId, "Carl");

		// Carl shares it with Bob
		secretService.shareSecret("Carl", aliceSecretId, "Bob");

		// Alice unshares it with Bob
		secretService.unshareSecret("Alice", aliceSecretId, "Bob");

		// Bob cannot read this secret anymore
		secretService.readSecret("Bob", aliceSecretId);
	}

	/**
	 * TestG: Alice shares a secret with Bob; Bob shares it with Carl, and then unshares it with Carl. Carl can still read this secret, as Bob didn't create this secret.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Carl can still read this secret.
	 */
	@Test
	public void testG() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testG");

		// Alice shares a secret with Bob
		UUID aliceSecretId = secretService.createSecret("Alice", "My little secret");
		secretService.shareSecret("Alice", aliceSecretId, "Bob");

		// Bob shares it with Carl
		secretService.shareSecret("Bob", aliceSecretId, "Carl");

		// Bob unshares it with Carl
		secretService.unshareSecret("Bob", aliceSecretId, "Carl");

		// Carl can still read this secret
		secretService.readSecret("Carl", aliceSecretId);
	}

	/**
	 * TestH: Alice shares a secret with Bob; Carl unshares it with Bob, and encounters UnauthorizedException.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Unauthorized exception occurs.
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testH() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testH");

		// Alice shares a secret with Bob
		UUID aliceSecretId = secretService.createSecret("Alice", "My little secret");
		secretService.shareSecret("Alice", aliceSecretId, "Bob");

		// Carl unshares it with Bob, and encounters UnauthorizedException
		secretService.unshareSecret("Carl", aliceSecretId, "Bob");
	}

	/**
	 * TestI: Alice shares a secret with Bob; Bob shares it with Carl; Alice unshares it with Bob; Bob shares it with Carl with again, and encounters UnauthorizedException.
	 * 
	 * @throws IOException
	 * @throws NotAuthorizedException
	 * @throws IllegalArgumentException
	 * @result Unauthorized exception occurs.
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testI() throws IllegalArgumentException, NotAuthorizedException, IOException {

		System.out.println("testI");

		// Alice shares a secret with Bob
		UUID aliceSecretId = secretService.createSecret("Alice", "My little secret");
		secretService.shareSecret("Alice", aliceSecretId, "Bob");

		// Bob shares it with Carl
		secretService.shareSecret("Bob", aliceSecretId, "Carl");

		// Alice unshares it with Bob
		secretService.unshareSecret("Alice", aliceSecretId, "Bob");

		// Bob shares it with Carl again and encounters UnauthorizedException.
		secretService.shareSecret("Bob", aliceSecretId, "Carl");
	}

	/**
	 * TestJ: Alice stores the same secret object twice, and get two different UUIDs.
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result Alice gets two different UUIDs.
	 */
	@Test
	public void testJ() throws IllegalArgumentException, IOException {

		System.out.println("testJ");

		// Alice stores the secret
		UUID aliceSecretId1 = secretService.createSecret("Alice", "My little secret");

		// Alice stores again the same secret
		UUID aliceSecretId2 = secretService.createSecret("Alice", "My little secret");

		// Alice get two different UUIDs
		assertNotEquals(aliceSecretId1, aliceSecretId2);
	}

	/**
	 * TestK: null as an argument in the different methods
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result IllegalArgumentException thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testK() throws IllegalArgumentException, IOException {

		System.out.println("testK");

		// Alice stores the secret
		UUID aliceSecretId1 = secretService.createSecret("Alice", "My little secret");
		// UUID aliceSecretId1 = secretService.createSecret("Alice", null);
		secretService.shareSecret("Alice", aliceSecretId1, "Bob");
		// secretService.unshareSecret("Alice", aliceSecretId1, "Bob");
		secretService.readSecret("Bob", null);

	}

	/**
	 * TestL: Network Failure - To run this test, add the following under readSecret() method in the SecretServiceImpl.java file - throw new IOException();
	 * 
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result Alice gets two different UUIDs.
	 */
//	@Test(expected = IOException.class)
//	public void testL() throws IllegalArgumentException, IOException {
//
//		System.out.println("testL");
//
//		// Alice stores the secret
//		UUID aliceSecretId1 = secretService.createSecret("Alice", "My little secret");
//		// UUID aliceSecretId1 = secretService.createSecret("Alice", null);
//		secretService.shareSecret("Alice", aliceSecretId1, "Bob");
//		// secretService.unshareSecret("Alice", aliceSecretId1, "Bob");
//		secretService.readSecret("Bob", aliceSecretId1);
//
//	}

	/**
	 * TestM: Reset all - Alice shares a secret with Bob. Bob reads the secret. Then system is reset. Bob should not be able to read the secret
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result NotAuthorizedException thrown
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testM() throws IllegalArgumentException, IOException {

		System.out.println("testM");

		UUID secret = secretService.createSecret("Alice", "My little secret");
		secretService.shareSecret("Alice", secret, "Bob");
		secretService.readSecret("Bob", secret);
		stats.resetStatsAndSystem();
		secretService.readSecret("Bob", secret);

	}

	/**
	 * TestN: Get the different secret stats - Alice creates "My little secret" and shares a secret with Air. Alice creates "A little secret" and shares a secret with Ben. Ben shares this secret with Air. Air shares Alice's secret with Ben, Carl, Fred,
	 * Joey, and Tim.
	 * 
	 * Most Trusted User: Ben is shared with 2 secrets. Air is shared with 2 secrets. Others are shared with 1 secret. Therefore alphabetically - Air. Unsharing does not affect this stat.
	 * 
	 * Worst Secret Keeper: Alice shared secret with 2 users. Alice was not shared with any secret. = -2 Ben shared secret with 1 user. Ben was shared with 2 secrets. = 1. Air shared secret with 5 users. Air was shared with 2 secrets. = -3. All other
	 * users are shared with 1 secret. Therefore worst secret keeper - Air
	 * 
	 * Best known secret: "My little secret" is shared with 4 users and "A little secret" is shared with 4 users. Therefore best known is "A little secret" alphabetically.
	 * 
	 * Length of Longest Secret - 16 - "My little secret"
	 * 
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result NotAuthorizedException thrown
	 */
	@Test
	public void testN() throws IllegalArgumentException, IOException {

		System.out.println("testN");

		UUID secret = secretService.createSecret("Alice", "My little secret");
		UUID secret1 = secretService.createSecret("Alice", "A little secret");
		secretService.shareSecret("Alice", secret, "Air");
		secretService.shareSecret("Alice", secret1, "Ben");
		secretService.shareSecret("Ben", secret1, "Air");
		secretService.shareSecret("Air", secret, "Ben");
		secretService.shareSecret("Air", secret1, "Aarl");
		secretService.shareSecret("Air", secret, "Fred");
		secretService.shareSecret("Air", secret1, "Joey");
		secretService.shareSecret("Air", secret, "Tim");
		secretService.unshareSecret("Alice", secret, "Air");
		secretService.unshareSecret("Alice", secret1, "Air");
		String bestKnown = stats.getBestKnownSecret();
		int longest = stats.getLengthOfLongestSecret();
		String mostTrusted = stats.getMostTrustedUser();
		String worstSecretKeeper = stats.getWorstSecretKeeper();
//		System.out.println(bestKnown);
//		System.out.println(longest);
//		System.out.println(worstSecretKeeper);
//		System.out.println(mostTrusted);
		assertEquals(bestKnown, "A little secret");
		assertEquals(longest, 16);
		assertEquals(mostTrusted, "Air");
		assertEquals(worstSecretKeeper, "Air");
	}

	/**
	 * TestO: If no secrets are shared, then value of all stats except the longest secret stat, should be null.
	 * 
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result NotAuthorizedException thrown
	 */
	@Test
	public void testO() throws IllegalArgumentException, IOException {

		System.out.println("testO");

		UUID secret = secretService.createSecret("Alice", "My little secret");
		UUID secret1 = secretService.createSecret("Alice", "A little secret");
		String bestKnown = stats.getBestKnownSecret();
		int longest = stats.getLengthOfLongestSecret();
		String mostTrusted = stats.getMostTrustedUser();
		String worstSecretKeeper = stats.getWorstSecretKeeper();
		assertEquals(bestKnown, null);
		assertEquals(longest, 16);
		assertEquals(mostTrusted, null);
		assertEquals(worstSecretKeeper, null);
	}

	/**
	 * TestP: Unsharing a previously shared secret should not affect the Most trusted user and Worst Secret Keeper stats, as in the docs it is written that unsharing does not affect the sharing occurences count.
	 * 
	 * Most Trusted User: Ben
	 * 
	 * Worst Secret Keeper: Alice
	 * 
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result NotAuthorizedException thrown
	 */
	@Test
	public void testP() throws IllegalArgumentException, IOException {

		System.out.println("testP");

		UUID secret = secretService.createSecret("Alice", "My little secret");
		secretService.shareSecret("Alice", secret, "Ben");
		secretService.unshareSecret("Alice", secret, "Ben");
		String mostTrusted = stats.getMostTrustedUser();
		String worstSecretKeeper = stats.getWorstSecretKeeper();
		assertEquals(mostTrusted, "Ben");
		assertEquals(worstSecretKeeper, "Alice");
	}

	/**
	 * TestQ: Throw IllegalArgumentException when length of secret exceeds 100 - Secret "Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum will now end.." has 101 characters.
	 *
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result NotAuthorizedException thrown
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testQ() throws IllegalArgumentException, IOException {

		System.out.println("testQ");

		UUID secret = secretService.createSecret("Alice",
				"Lorem Ipsum is simply dummy text of the printing and typesetting industry. Lorem Ipsum will now end..");

	}

	/**
	 * TestR: Create a new secret object, without any user. Sharing this user object should throw NotAuthorizedException as it was not created by that user.
	 *
	 * @throws IOException
	 * @throws IllegalArgumentException
	 * @result NotAuthorizedException thrown
	 */
	@Test(expected = NotAuthorizedException.class)
	public void testR() throws IllegalArgumentException, IOException {

		System.out.println("testR");

		UUID secret = new UUID(0, 0);
		secretService.shareSecret("Alice", secret, "Ben");
//		secretService.shareSecret("Alice", secret, "Ben");
//		secretService.readSecret("Ben", secret);

	}

}