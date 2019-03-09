package edu.sjsu.cmpe275.aop.aspect;

import java.util.HashMap;
import java.util.HashSet;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.After;
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

	@After("mostTrustedUserPointcut()")
	public void mostTrustedUserAdvice(JoinPoint joinPoint) {
		System.out.printf("\nAfter the execution of the method %s\n", joinPoint.getSignature().getName());
		String sharerId = joinPoint.getArgs()[0].toString();
		String secretId = joinPoint.getArgs()[1].toString();
		String targetId = joinPoint.getArgs()[2].toString();
		System.out.printf("\nXXXXXXXXXXXXX %s %s %s\n", sharerId, secretId, targetId);

		HashMap<String, HashSet<String>> innerHashMap = new HashMap<String, HashSet<String>>();
		HashSet<String> innerHashSet = new HashSet<String>();

		if (sharerId == targetId) {
			return;
		}

		if (stats.sharedSecrets.containsKey(targetId)) {
			// If targetId present
			innerHashMap = stats.sharedSecrets.get(targetId);
			if (innerHashMap.containsKey(sharerId)) {
				// If sharer id present inside the hashmap of target id
				innerHashSet = innerHashMap.get(sharerId);
				innerHashSet.add(secretId);
				// An element is added to hashset only when it is not already present. Otherwise it returns false. Therefore we dont need to check whether the key secretId is already present in the set or not.
			} else {
				// If sharer id not present inside the hashmap of target id
				innerHashSet.add(secretId);
				innerHashMap.put(sharerId, innerHashSet);
			}
		} else {
			// If targetId not present
			innerHashSet.add(secretId);
			innerHashMap.put(sharerId, innerHashSet);
			stats.sharedSecrets.put(targetId, innerHashMap);
		}

		// stats.resetStats();
	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..))")
	public void mostTrustedUserPointcut() {
	}

}
