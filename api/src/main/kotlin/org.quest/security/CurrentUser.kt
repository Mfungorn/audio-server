package org.quest.security

import org.springframework.security.core.annotation.AuthenticationPrincipal
import java.lang.annotation.ElementType

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@MustBeDocumented
@AuthenticationPrincipal
annotation class CurrentUser {
}