package edu.sjsu.cmpe275.aop.aspect;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(0)
public class StatsAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Autowired
	SecretStatsImpl stats;

	// XXXXXXXXXXXXXXXXX AFTER RETURNING CORRECT?
	@AfterReturning("mostTrustedUserPointcut()")
	public void mostTrustedUserAdvice(JoinPoint joinPoint) {
		System.out.printf("\nAfter the execution of the method %s\n", joinPoint.getSignature().getName());
		String sharerId = joinPoint.getArgs()[0].toString();
		String secretId = joinPoint.getArgs()[1].toString();
		String targetId = joinPoint.getArgs()[2].toString();
		// System.out.printf("\nXXXXXXXXXXXXX %s %s %s\n", sharerId, secretId, targetId);

		HashMap<String, HashSet<String>> innerHashMap = new HashMap<String, HashSet<String>>();
		HashSet<String> innerHashSet = new HashSet<String>();

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

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
	public void mostTrustedUserPointcut() {
	}

	// XXXXXXXXXXXXXXXXX AFTER RETURNING CORRECT?
	@AfterReturning(pointcut = "execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))", returning = "returnValue")
	public void bestKnownSecretAdvice(JoinPoint joinPoint, Object returnValue) { // This is the syntax when we need to use the return value of the method that the advice is applied upon
		System.out.printf("\nKKKKKKKKKKKKKK After the execution of the method %s\n",
				joinPoint.getSignature().getName());

		if (stats.permanentNetworkFailure == true) {
			return;
		}

		HashSet<String> innerHashSet = new HashSet<String>();
		ArrayList<String> creatorAndContent = new ArrayList<String>();
		String userId = null, secretContent = null, sharerId = null, secretId = null;

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

		// For bestknownsecret:
		if (stats.knownSecrets.containsKey(secretContent)) {
			(stats.knownSecrets.get(secretContent)).add(userId);
			// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key userid is already present in the set or not.
		} else {
			// If secret id not present
			innerHashSet.add(userId);
			stats.knownSecrets.put(secretContent, innerHashSet);
		}

		// For worstSecretKeeper:
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

		}

		// stats.resetStats();
	}

}
