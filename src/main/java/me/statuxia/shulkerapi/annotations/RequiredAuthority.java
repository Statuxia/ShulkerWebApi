package me.statuxia.shulkerapi.annotations;

import me.statuxia.shulkerapi.model.TokenAuthorityEnum;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequiredAuthority {

    TokenAuthorityEnum[] requireAll() default {};

    TokenAuthorityEnum[] requireAny() default {};
}
