package com.team5.studygroup.user

import com.team5.studygroup.user.model.CustomUserDetails
import com.team5.studygroup.user.model.User
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

@Component
class LoggedInUserResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasAnnotation = parameter.hasParameterAnnotation(LoggedInUser::class.java)
        val type = parameter.parameterType

        // @LoggedInUser가 붙어있고, 타입이 Long이거나 User인 경우 지원
        return hasAnnotation && (
            type == Long::class.java ||
                type == Long::class.javaObjectType ||
                type == User::class.java
        )
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Any? {
        val authentication = SecurityContextHolder.getContext().authentication
        val principal = authentication?.principal

        if (principal is CustomUserDetails) {
            return if (parameter.parameterType == User::class.java) {
                principal.getUserEntity()
            } else {
                principal.id
            }
        }

        return null
    }
}
