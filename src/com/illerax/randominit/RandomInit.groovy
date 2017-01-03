package com.illerax.randominit

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

/**
 * Created by Evgeny Smirnov on 01/03/17.
 */
@Target([ElementType.LOCAL_VARIABLE])
@Retention(RetentionPolicy.SOURCE)
@GroovyASTTransformationClass("com.illerax.randominit.RandomInitTransformation")
@interface RandomInit {
    int minLength() default 1

    int maxLength() default 100

    int min() default 0

    int max() default 1
}