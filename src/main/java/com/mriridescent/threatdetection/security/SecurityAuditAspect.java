package com.mriridescent.threatdetection.security;

import com.mriridescent.threatdetection.audit.AuditLogger;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * Security audit aspect that automatically logs security-relevant operations.
 * Provides comprehensive audit trail for compliance requirements (SOC2, GDPR, etc.)
 */
@Aspect
@Component
@RequiredArgsConstructor
public class SecurityAuditAspect {

    private final AuditLogger auditLogger;

    /**
     * Log attempts to access secured methods
     */
    @Before("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void logSecuredMethodAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String resourceType = className;
        String resourceId = methodName;

        // Extract the PreAuthorize expression if possible
        String accessDetails = "";
        try {
            Method method = getMethod(joinPoint);
            if (method != null && method.isAnnotationPresent(PreAuthorize.class)) {
                PreAuthorize annotation = method.getAnnotation(PreAuthorize.class);
                accessDetails = annotation.value();
            }
        } catch (Exception e) {
            // Ignore exceptions in audit logging
        }

        auditLogger.logSecurityEvent(
                AuditLogger.EventType.AUTHORIZATION,
                resourceType,
                resourceId,
                "ACCESS",
                "ATTEMPT",
                "Required authorization: " + accessDetails
        );
    }

    /**
     * Log successful execution of secured methods
     */
    @AfterReturning("@annotation(org.springframework.security.access.prepost.PreAuthorize)")
    public void logSuccessfulSecuredMethodAccess(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        auditLogger.logSecurityEvent(
                AuditLogger.EventType.AUTHORIZATION,
                className,
                methodName,
                "ACCESS",
                "SUCCESS",
                ""
        );
    }

    /**
     * Log failed execution of secured methods
     */
    @AfterThrowing(pointcut = "@annotation(org.springframework.security.access.prepost.PreAuthorize)", throwing = "ex")
    public void logFailedSecuredMethodAccess(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        auditLogger.logSecurityEvent(
                AuditLogger.EventType.AUTHORIZATION,
                className,
                methodName,
                "ACCESS",
                "FAILURE",
                "Exception: " + ex.getClass().getSimpleName() + ": " + ex.getMessage()
        );
    }

    /**
     * Helper method to get the Method object from a JoinPoint
     */
    private Method getMethod(JoinPoint joinPoint) {
        try {
            String methodName = joinPoint.getSignature().getName();
            Class<?> targetClass = joinPoint.getTarget().getClass();

            for (Method method : targetClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
        } catch (Exception e) {
            // Ignore
        }
        return null;
    }
}
