package edu.sjsu.cmpe275.aop.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
@Order(1)
public class AccessControlAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Before("accessControlPointcut()")
	public void accessControlAdvice(JoinPoint joinPoint) {
		System.out.printf("Access control prior to the execution of the method %s\n",
				joinPoint.getSignature().getName());
	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void accessControlPointcut() {
	}

}
