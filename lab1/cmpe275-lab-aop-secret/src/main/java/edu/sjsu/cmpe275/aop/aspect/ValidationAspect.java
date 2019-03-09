package edu.sjsu.cmpe275.aop.aspect;

import java.util.UUID;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;

import edu.sjsu.cmpe275.aop.SecretStatsImpl;

@Aspect
@Order(1)
public class ValidationAspect {
	/***
	 * Following is a dummy implementation of this aspect. You are expected to provide an actual implementation based on the requirements, including adding/removing advices as needed.
	 */

	@Autowired
	SecretStatsImpl stats;

	@Around("validateCreateSecretPointcut()")
	public UUID validateCreateSecretAdvice(ProceedingJoinPoint joinPoint) throws Throwable {

		System.out.printf("Doing validation prior to the execution of the method %s\n\n",
				joinPoint.getSignature().getName());

		// joinPoint.getArgs().length - length of the getArgs array - to get the number of arguments passed

		// joinPoint.getArgs()[0]: Object containing the userid passed. Validation - User id object can be null or user id object can contain a string which is empty
		// joinPoint.getArgs()[1]: Object containing the secretContent passed. Validation - secretContent object can be null or secretContent string can be more than 100 characters. We dont check if secretContent string is empty

		// XXXXXXXXXXXXXXXXXXXXXXXX RESOLVED - REMOVE CHECK FOR USEDID STRING BEING EMPTY?
		// XXXXXXXXXXXXXXXXXXXXXXXX CHECK FOR USEDID BEING UNIQUE?
		// XXXXXXXXXXXXXXXXXXXXXXXX RESOLVED - CHECK FOR SECRET CONTENT STRING BEING EMPTY?
		// XXXXXXXXXXXXXXXXXXXXXXXX RESOLVED - CHECK FOR SECRET CONTENT OBJECT BEING NULL?
		// XXXXXXXXXXXXXXXXXXXXXXXX RESOLVED - COUNT NEXTLINE CHAR/ENTER/LINE BREAK AS A CHAR IN SECRET CONTENT? - ECLIPSE IS INTRODUCING NEXTLINE CHAR BY ITSELF, IF THE MESSAGE TYPED HAD SOME ENTER PRESSES - CHECK FOR "\r\n", "\n"?
		// XXXXXXXXXXXXXXXXXXXXXXXX RESOLVED - COUNT NEXTLINE CHAR/ENTER AS A CHAR IN SECRET?
		// XXXXXXXXXXXXXXXXXXXXXXXX RESOLVED - REMOVE STARTING OR ENDING WHITESPACES?
		// XXXXXXXXXXXXXXXXXXXXXXXX IS throws throwable REQUIRED EVERYWHERE?

		if (joinPoint.getArgs()[0] == null) {
			// System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX USER ID IS NULL!!!!!!!\n\n");
			throw new IllegalArgumentException();
		}
//			else if (joinPoint.getArgs()[0].toString().length() == 0) {
//			System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX USER ID IS EMPTY!!!!!!!\n\n");
//			throw new IllegalArgumentException("USER ID IS EMPTY");
//		}

//		if (joinPoint.getArgs()[1] == null) {
//			System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX SECRET CONTENT IS NULL!!!!!!!\n\n");
//		} else if (joinPoint.getArgs()[1].toString().length() > 100) {
//			System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX SECRET CONTENT EXCEEDS CHARACTER LIMIT!!!!!!!!\n\n");
//		}
		if (joinPoint.getArgs()[1] != null) {
			int secretContentLength = joinPoint.getArgs()[1].toString().length();
			if (secretContentLength > 100) {
				// System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX SECRET CONTENT EXCEEDS CHARACTER LIMIT!!!!!!!!\n\n");
				throw new IllegalArgumentException();
				// XXXXXXXXXXXXXXXXXXXXXXXX USING e.printstackthrow INSTEAD OF throw new?
			} else if (secretContentLength > stats.lengthOfLongestSecret) {
				stats.lengthOfLongestSecret = secretContentLength;
				Object[] args = joinPoint.getArgs();
				return (UUID) joinPoint.proceed(args);
				// Statsaspect uses @AfterReturning on createSecret and validationaspect uses @Around on createSecret. @Around will run first. We want returnValue of createSecret into @AfterReturning therefore we must pass the returnValue through @Around return
			} else {
				Object[] args = joinPoint.getArgs();
				return (UUID) joinPoint.proceed(args);
			}
		} else {
			Object[] args = joinPoint.getArgs();
			return (UUID) joinPoint.proceed(args);
		}

	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.createSecret(..))") // XXXXXXXXXXXXXXXXXXXXXXXXX should remove public in all?
	public void validateCreateSecretPointcut() {
	}

	@Before("validateReadSecretPointcut()")
	public void validateReadSecretAdvice(JoinPoint joinPoint) throws Throwable {

		System.out.printf("\nDoing validation prior to the execution of the method %s\n\n",
				joinPoint.getSignature().getName());

		if (joinPoint.getArgs()[0] == null) {
			// System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX USER ID OF TARGET USER IS NULL!!!!!!!\n\n");
			throw new IllegalArgumentException();
		}
		if (joinPoint.getArgs()[1] == null) {
			// System.out.printf("XXXXXXXXXXXXXXXXXXXXXXXX SECRET IS NULL!!!!!!!\n\n");
			throw new IllegalArgumentException();
		}

	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.readSecret(..))")
	public void validateReadSecretPointcut() {
	}

	@Around("validateShareOrUnshareSecretPointcut()")
	public void validateShareOrUnshareSecretAdvice(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.printf("\nDoing validation prior to the execution of the method %s\n\n",
				joinPoint.getSignature().getName());

		if (joinPoint.getArgs()[0] == null || joinPoint.getArgs()[1] == null || joinPoint.getArgs()[2] == null) {
			throw new IllegalArgumentException();
		} else {
			Object[] args = joinPoint.getArgs();
			System.out.println("RRRRRRRRRRRRRRRRR");
			joinPoint.proceed(args);
		}

	}

	@Pointcut("execution(public * edu.sjsu.cmpe275.aop.SecretService.shareSecret(..)) || execution(public * edu.sjsu.cmpe275.aop.SecretService.unshareSecret(..))")
	public void validateShareOrUnshareSecretPointcut() {
	}

}
