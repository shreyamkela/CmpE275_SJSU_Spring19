/**
* @author Shreyam Kela - Student Id 013775411
*/

package edu.sjsu.cmpe275.aop.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(1) // By specifying the order number we can control which aspect runs first at any particular joinpoint, if there are clashing aspects wanting to run on the same joinpoint
public class ValidationAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Autowired
	SecretStatsImpl stats;

	@Around("execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))")
	public Object validateCreateSecretAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

		if (stats.permanentNetworkFailure == true) { // Possibly this check is not required as we are throwing an exception if there is anetwork failure and not proceeding. We wont even reach this advice if there is a network failure
			return null;
		}

//		System.out.printf("Doing validation prior to the execution of the method %s\n\n",
//				joinPoint.getSignature().getName());

		// joinPoint.getArgs().length - length of the getArgs array - to get the number of arguments passed
		// joinPoint.getArgs()[0]: Object containing the userid passed. Validation - User id object can be null or user id object can contain a string which is empty
		// joinPoint.getArgs()[1]: Object containing the secretContent passed. Validation - secretContent object can be null or secretContent string can be more than 100 characters. We dont check if secretContent string is empty

		if (joinPoint.getArgs()[0] == null) {
			throw new IllegalArgumentException();
		}

		if (joinPoint.getArgs()[1] != null) {
			int secretContentLength = joinPoint.getArgs()[1].toString().length();
			if (secretContentLength > 100) {
				throw new IllegalArgumentException();
			} else if (secretContentLength > stats.lengthOfLongestSecret) {
				stats.lengthOfLongestSecret = secretContentLength;
				Object[] args = joinPoint.getArgs();
				return joinPoint.proceed(args);
				// IMPORTANT - StatsAspect uses @AfterReturning on createSecret and validationaspect uses @Around on createSecret. @Around will run first. We want returnValue of createSecret into @AfterReturning therefore we must pass the returnValue through @Around
				// return
				// Also, if there are 2 joinpoints that can run before or after or around a method, one after the other, then it it very important to take care of passing the method arguments (and returnValue of the method if required) between the joinpoints, then
				// only the successor joinpoint would be able to use the args and returnValue
			} else {
				Object[] args = joinPoint.getArgs();
				return joinPoint.proceed(args);
			}
		} else {
			throw new IllegalArgumentException();
		}

	}

	@Around("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
	public Object validateReadSecretAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

		if (stats.permanentNetworkFailure == true) {
			return null;
		}

//		System.out.printf("\nDoing validation prior to the execution of the method %s\n\n",
//				joinPoint.getSignature().getName());

		if (joinPoint.getArgs()[0] == null || joinPoint.getArgs()[1] == null) {
			throw new IllegalArgumentException();
		} else {
			Object[] args = joinPoint.getArgs();
			return joinPoint.proceed(args);
		}

	}

	@Around("execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void validateShareOrUnshareSecretAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

		if (stats.permanentNetworkFailure == true) {
			return;
		}

//		System.out.printf("\nDoing validation prior to the execution of the method %s\n\n",
//				joinPoint.getSignature().getName());

		if (joinPoint.getArgs()[0] == null || joinPoint.getArgs()[1] == null || joinPoint.getArgs()[2] == null) {

			throw new IllegalArgumentException();
		} else {

			Object[] args = joinPoint.getArgs();
			joinPoint.proceed(args);
		}

	}

}
