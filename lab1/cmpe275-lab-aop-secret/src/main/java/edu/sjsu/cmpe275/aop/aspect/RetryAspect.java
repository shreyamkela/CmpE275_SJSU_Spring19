package edu.sjsu.cmpe275.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;

@Aspect
@Order(1)
public class RetryAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Around("networkFailureRetryPointcut()")
	public Object networkFailureRetryAdvice(ProceedingJoinPoint joinPoint) throws Throwable { // @around should always return something an object
		System.out.printf("XXXXXXXXXXXXXXXX Retry aspect prior to the execution of the method %s\n",
				joinPoint.getSignature().getName());

		joinPoint.proceed();
		try {
			return joinPoint.proceed();
//			System.out.printf("Finished the execution of the method %s with result %s\n",
//					joinPoint.getSignature().getName(), result);
		} catch (Throwable e) {
			e.printStackTrace();
			return null;
//			System.out.printf("Aborted the execution of the method %s\n", joinPoint.getSignature().getName());
		}
	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void networkFailureRetryPointcut() {
	}

}
