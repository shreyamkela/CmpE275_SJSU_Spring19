package edu.sjsu.cmpe275.aop.aspect;

import java.io.IOException;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(0) // By specifying the order number we can control which aspect runs first at any particular joinpoint, if there are clashing aspects wanting to run on the same joinpoint
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
		String raisedException = null, requiredException = "IOException";

		try {
			Object[] args = joinPoint.getArgs();
			return joinPoint.proceed(args);
		} catch (Throwable e1) { // Note - This cathces IOexception as well any other exceptions thrown by other advices that run together with this advice
			raisedException = e1.toString();
			if (raisedException.toLowerCase().contains(requiredException.toLowerCase())) { // If the thrown exception is IOexception, then only retry, otherwise it must be an argument or authorization exception and we shouldnt be retrying then.
				try {
					System.out.printf("Retrying %s for the 1st time\n", joinPoint.getSignature().getName());
					Object[] args = joinPoint.getArgs();
					return joinPoint.proceed(args);

				} catch (Throwable e2) {
					raisedException = e2.toString();
					if (raisedException.toLowerCase().contains(requiredException.toLowerCase())) {
						try {
							System.out.printf("Retrying %s for the 2nd time\n", joinPoint.getSignature().getName());
							Object[] args = joinPoint.getArgs();
							return joinPoint.proceed(args);
						} catch (Throwable e3) {
							raisedException = e3.toString();
							if (raisedException.toLowerCase().contains(requiredException.toLowerCase())) {
								System.out.printf(
										"Aborted the execution of the method %s due to permanent network failure\n",
										joinPoint.getSignature().getName());
								stats.permanentNetworkFailure = true;

								// XXXXXXXXXXXXXXXXXXXXXXXXXXX THROW NEW IOEXCEPTION?
								// XXXXXXXXXXXXXXXXXXXXXXXXXXX CHECK INTERMIXED ASPECTS EXCEPTIONS? DO I CHECK FOR VALIDATION ASPECT HERE & VICE VERSA
								// e3.printStackTrace();
								// return null;
								throw new IOException();
							}

						}
					}

				}
			} else { // If exception is something other than IOexception i/e network exception

				throw e1;
			}
		}
		return null;
	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.*(..))")
	public void networkFailureRetryPointcut() {
	}

}
