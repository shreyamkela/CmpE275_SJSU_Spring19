package edu.sjsu.cmpe275.aop.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(4) // By specifying the order number we can control which aspect runs first at any particular joinpoint, if there are clashing aspects wanting to run on the same joinpoint
public class StatsAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Autowired
	SecretStatsImpl stats;

	// XXXXXXXXXXXXXXXXX AFTER RETURNING CORRECT?
	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))", returning = "returnValue")
	public void statsAdvice(JoinPoint joinPoint, Object returnValue) { // This is the syntax when we need to use the return value of the method that the advice is applied upon
		System.out.printf("\nAfter the execution of the method %s\n", joinPoint.getSignature().getName());
// After returning is used therefore we dont have to check for network failure
		HashSet<String> innerHashSet = new HashSet<String>();
		ArrayList<String> creatorAndContent = new ArrayList<String>();
		String userId = null, secretContent = null, sharerId = null, secretId = null;

		if (stats.permanentNetworkFailure == true) { // Possibly this check is not required as we are throwing an exception if there is anetwork failure and not proceeding. We wont even reach this advice if there is a network failure
			return;
		}

		if (joinPoint.getArgs().length == 2) { // The method is createSecret
			userId = joinPoint.getArgs()[0].toString(); // This user gets to know this secret
			secretContent = joinPoint.getArgs()[1].toString();
			creatorAndContent.add(userId); // Adding the creator of this secret
			creatorAndContent.add(secretContent); // Adding the secret content

			stats.secretIdWithCreatorAndContent.put(returnValue.toString(), creatorAndContent); // returnValue stores the return value of the createSecret method i.e the uuid. secretIdWithContent stores the uuid string with the creator id and secret contents
		} else {// The method is shareSecrect

			userId = joinPoint.getArgs()[2].toString(); // This user gets to know this secret - this user is target user
			secretContent = stats.secretIdWithCreatorAndContent.get(joinPoint.getArgs()[1].toString()).get(1); // joinPoint.getArgs()[1] here is the secret id i.e UUID.
			sharerId = joinPoint.getArgs()[0].toString();
		}

		// FOR BEST KNOWN SECRET
		if (stats.knownSecrets.containsKey(secretContent)) {
			(stats.knownSecrets.get(secretContent)).add(userId);
			// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key userid is already present in the set or not.
		} else {
			// If secret id not present
			innerHashSet.add(userId);
			stats.knownSecrets.put(secretContent, innerHashSet);
		}

		// FOR WORST SECRET KEEPER
		if (joinPoint.getArgs().length == 3) {
			if (sharerId == userId) {
				return;
			}
			secretId = joinPoint.getArgs()[1].toString();

			HashMap<String, HashSet<String>> innerHashMap = new HashMap<String, HashSet<String>>();
			HashSet<String> innerHashSet1 = new HashSet<String>();

			if (stats.creatorSecrets.containsKey(sharerId)) {
				// If sharerId present
				innerHashMap = stats.creatorSecrets.get(sharerId);
				if (innerHashMap.containsKey(userId)) {
					// If user/target id present inside the hashmap of sharer id
					//
//					innerHashSet1 = innerHashMap.get(userId);
//					innerHashSet1.add(secretId);
					stats.creatorSecrets.get(sharerId).get(userId).add(secretId);
					// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key secretId is already present in the set or not.
				} else {
					// If user id not present inside the hashmap of sharer id
					// System.out.printf("\nCCCCCCCCCCCC %s %s %s\n", sharerId, secretId, targetId);
					innerHashSet1.add(secretId);
					// innerHashMap.put(sharerId, innerHashSet1);
					stats.creatorSecrets.get(sharerId).put(sharerId, innerHashSet1);
				}
			} else {
				// If sharer not present
				innerHashSet1.add(secretId);
				innerHashMap.put(userId, innerHashSet1);
				stats.creatorSecrets.put(sharerId, innerHashMap);
			}

			// FOR MOST TRUSTED USER
			System.out.printf("\nAfter the execution of the method %s\n", joinPoint.getSignature().getName());

			sharerId = joinPoint.getArgs()[0].toString();
			secretId = joinPoint.getArgs()[1].toString();
			String targetId = joinPoint.getArgs()[2].toString();
			// System.out.printf("\nXXXXXXXXXXXXX %s %s %s\n", sharerId, secretId, targetId);

			innerHashMap = new HashMap<String, HashSet<String>>();
			innerHashSet = new HashSet<String>();

			if (sharerId == targetId || stats.permanentNetworkFailure == true) {
				return;
			}

			if (stats.sharedSecrets.containsKey(targetId)) {
				// System.out.printf("\nAAAAAAAAAAAAA %s %s %s\n", sharerId, secretId, targetId);

				// If targetId present
				innerHashMap = stats.sharedSecrets.get(targetId);
				if (innerHashMap.containsKey(sharerId)) {
					// If sharer id present inside the hashmap of target id
					// System.out.printf("\nBBBBBBBBBBBB %s %s %s\n", sharerId, secretId, targetId);
					innerHashSet = innerHashMap.get(sharerId);
					innerHashSet.add(secretId);
					// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key secretId is already present in the set or not.
				} else {
					// If sharer id not present inside the hashmap of target id
					// System.out.printf("\nCCCCCCCCCCCC %s %s %s\n", sharerId, secretId, targetId);
					innerHashSet.add(secretId);
					innerHashMap.put(sharerId, innerHashSet);
				}
			} else {
				// If targetId not present
				// System.out.printf("\nDDDDDDDDDDDDDDD %s %s %s\n", sharerId, secretId, targetId);
				innerHashSet.add(secretId);
				innerHashMap.put(sharerId, innerHashSet);
				stats.sharedSecrets.put(targetId, innerHashMap);
			}

			// stats.resetStats();

		}

		// For access control: createSecret, shareSecret
		if (stats.permanentNetworkFailure == true) {
			return;
		}

		innerHashSet = new HashSet<String>();

		if (joinPoint.getArgs().length == 2) { // createSecret
			userId = joinPoint.getArgs()[0].toString(); // This user gets to know this secret - this user is target user
			secretId = returnValue.toString();
			if (stats.accessToSecrets.containsKey(userId)) {
				(stats.accessToSecrets.get(userId)).add(secretId);
				// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key userid is already present in the set or not.
			} else {
				// If userid is not present
				innerHashSet.add(secretId);
				stats.accessToSecrets.put(userId, innerHashSet);
			}

		} else { // shareSecret
			userId = joinPoint.getArgs()[2].toString(); // This user gets to know this secret - this user is target user
			secretId = joinPoint.getArgs()[1].toString();
			sharerId = joinPoint.getArgs()[0].toString();

			if (sharerId == userId) { // If one tries to share a secret with themselves
				return;
			}

			if (stats.accessToSecrets.containsKey(userId)) {
				(stats.accessToSecrets.get(userId)).add(secretId);
				// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key userid is already present in the set or not.
			} else {
				// If userid is not present
				innerHashSet.add(secretId);
				stats.accessToSecrets.put(userId, innerHashSet);
			}

		}
	}

	// For access control: unshareSecret
	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..)))", returning = "returnValue")
	public void AccessControlUnshareSecretAdvice(JoinPoint joinPoint, Object returnValue) {
		System.out.printf("\nAfter the execution of the method %s\n", joinPoint.getSignature().getName());

		if (stats.permanentNetworkFailure == true) {
			return;
		}

		String targetId = null, sharerId = null, secretId = null;

		targetId = joinPoint.getArgs()[2].toString(); // The secret is being unshared with this user
		secretId = joinPoint.getArgs()[1].toString();
		sharerId = joinPoint.getArgs()[0].toString();

		if (sharerId == targetId) { // Unsharing with himself is ignored
			return;
		}

		// If A shares a secret with B. Then if B tries to unshare it with A, this should not happen. Therefore if A is the creator of a secret then he cannot be unshared with his own secret
		if (stats.secretIdWithCreatorAndContent.get(secretId) != null) {
			if (stats.accessToSecrets.containsKey(targetId)
					&& stats.secretIdWithCreatorAndContent.get(secretId).get(0) != targetId
					&& stats.secretIdWithCreatorAndContent.get(secretId).get(0) == sharerId) {
				// If targetid is not the creator himself then only he can unshare
				// If sharerId is the creator himself then only he can unshare
				// if targetId is present in accessToSecrets then try to remove secretid. If target id not present then no need to do anything
				(stats.accessToSecrets.get(targetId)).remove(secretId);
			}
		}

	}
}
