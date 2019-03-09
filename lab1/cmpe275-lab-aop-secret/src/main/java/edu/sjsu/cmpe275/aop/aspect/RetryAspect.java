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
public class RetryAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Autowired
	SecretStatsImpl stats;

	@Around("networkFailureRetryPointcut()")
	public Object networkFailureRetryAdvice(ProceedingJoinPoint joinPoint) throws Throwable { // @around should always return something an object
		System.out.printf("Retry aspect prior to the execution of the method %s\n", joinPoint.getSignature().getName());

		stats.permanentNetworkFailure = false;

		try {
			return joinPoint.proceed();
		} catch (Throwable e1) {

			try {
				System.out.printf("Retrying %s for the 1st time\n", joinPoint.getSignature().getName());
				return joinPoint.proceed();

			} catch (Throwable e2) {
				try {
					System.out.printf("Retrying %s for the 2nd time\n", joinPoint.getSignature().getName());
					return joinPoint.proceed();
				} catch (Throwable e3) {
					System.out.printf("Aborted the execution of the method %s due to permanent network failure\n",
							joinPoint.getSignature().getName());
					stats.permanentNetworkFailure = true;
					e3.printStackTrace();
					// XXXXXXXXXXXXXXXXXXXXXXXXXXX THROW NEW IOEXCEPTION?
					// XXXXXXXXXXXXXXXXXXXXXXXXXXX CHECK INTERMIXED ASPECTS EXCEPTIONS? DO I CHECK FOR VALIDATION ASPECT HERE & VICE VERSA
					return null;
				}
			}

		}
	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void networkFailureRetryPointcut() {
	}

}
