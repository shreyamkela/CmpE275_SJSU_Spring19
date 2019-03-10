package edu.sjsu.cmpe275.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.NotAuthorizedException;
import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(1)
public class AccessControlAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Autowired
	SecretStatsImpl stats;

	@Around("accessControlPointcut()")
	public Object accessControlAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

		if (stats.permanentNetworkFailure == true) {
			return null;
		}

		System.out.printf("\nAccess control prior to the execution of the method %s\n",
				joinPoint.getSignature().getName());

		String readerId = null, sharerUnsharerId = null, secretId = null;

		if (joinPoint.getArgs().length == 2) { // This method is readSecret

			readerId = joinPoint.getArgs()[0].toString();
			secretId = joinPoint.getArgs()[1].toString();

			if (stats.accessToSecrets.containsKey(readerId)) {
				if (stats.accessToSecrets.get(readerId).contains(secretId)) {
					Object[] args = joinPoint.getArgs();
					return joinPoint.proceed(args);
				} else {
					throw new NotAuthorizedException();
				}
			} else {
				throw new NotAuthorizedException();
			}

		} else {// This method is either shareSecret or unshareSecret

			sharerUnsharerId = joinPoint.getArgs()[0].toString();
			secretId = joinPoint.getArgs()[1].toString();

			if (stats.accessToSecrets.containsKey(sharerUnsharerId)) {
				if (stats.accessToSecrets.get(sharerUnsharerId).contains(secretId)) {
					Object[] args = joinPoint.getArgs();
					return joinPoint.proceed(args);
				} else {
					throw new NotAuthorizedException();
				}
			} else {
				throw new NotAuthorizedException();
			}

		}

	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void accessControlPointcut() {
	}

}
