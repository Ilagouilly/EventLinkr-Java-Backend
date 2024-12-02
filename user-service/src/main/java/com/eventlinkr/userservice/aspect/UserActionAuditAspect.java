package com.eventlinkr.userservice.aspect;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserActionAuditAspect {
    private static final Logger auditLogger = LoggerFactory.getLogger("AUDIT_LOGGER");

    @Before("execution(* com.eventlinkr.userservice.service.UserService.*(..))")
    public void logUserAction(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();

        // Basic audit logging with method name and arguments
        auditLogger.info("User Action: Method={}, Arguments={}", 
            methodName, 
            args.length > 0 ? args[0] : "No arguments"
        );
    }
}