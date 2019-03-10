package edu.sjsu.cmpe275.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

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

		System.out.printf("XXXXXXXXXXXXX Access control prior to the execution of the method %s\n",
				joinPoint.getSignature().getName());

		Object[] args = joinPoint.getArgs();
		return joinPoint.proceed(args);
	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void accessControlPointcut() {
	}

}
