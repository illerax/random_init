# random_init - Groovy Custom Annotation Example

Sample custom Groovy annotation with AST transformation.

Randomly initialize local variables.

**Usage:**
```
@RandomInit(min=1, max=300) Integer int1

@RandomInit(minLength = 10) String str1
```

Annotations are one of the best way to enhance language power. Build custom annotations in Groovy is pretty easy.

Lets create simple annotation that will randomly initialize annotated number or string variables. This annotation can be useful for example in testing.

First of all we need to declare annotation:

```
package com.illerax.randominit

import org.codehaus.groovy.transform.GroovyASTTransformationClass

import java.lang.annotation.ElementType
import java.lang.annotation.Retention
import java.lang.annotation.RetentionPolicy
import java.lang.annotation.Target

@Target([ElementType.LOCAL_VARIABLE])
@Retention(RetentionPolicy.SOURCE)
@GroovyASTTransformationClass("com.illerax.randominit.RandomInitTransformation")
@interface RandomInit {
    int minLength() default 1
    int maxLength() default 100
    int min() default 0
    int max() default 1
}
```

Then we need to create transformation code:
```
package com.illerax.randominit

import org.codehaus.groovy.ast.ASTNode
import org.codehaus.groovy.ast.expr.ConstantExpression
import org.codehaus.groovy.control.CompilePhase
import org.codehaus.groovy.control.SourceUnit
import org.codehaus.groovy.transform.ASTTransformation
import org.codehaus.groovy.transform.GroovyASTTransformation

/**
 * Created by Evgeny Smirnov on 01/03/17.
 */
@GroovyASTTransformation(phase = CompilePhase.SEMANTIC_ANALYSIS)
class RandomInitTransformation implements ASTTransformation {

    @Override
    void visit(ASTNode[] nodes, SourceUnit source) {
        Random random = new Random()
        def (annotation, expression) = nodes
        switch (expression.variableExpression.type.typeClass) {
            case Number:
                def min = annotation.members?.min?.value ?: RandomInit.getDeclaredMethod('min').defaultValue
                def max = annotation.members?.max?.value ?: RandomInit.getDeclaredMethod('max').defaultValue
                expression.rightExpression = new ConstantExpression(random.nextDouble() * (max - min) + min)
                break
            case String:
                String charset = (('a'..'z') + ('A'..'Z') + ('0'..'9') + (' ')).join()
                def min = annotation.members?.minLength?.value ?: RandomInit.getDeclaredMethod('minLength').defaultValue
                def max = annotation.members?.maxLength?.value ?: RandomInit.getDeclaredMethod('maxLength').defaultValue
                Integer len = random.nextInt(max - min + 1) + min
                expression.rightExpression = new ConstantExpression(
                        (1..len).inject('') { result, val -> result += charset[random.nextInt(charset.size())] }
                )
                break
        }
    }

}
```
Implementing ASTTransformation we must override visit method which will be called every time when compiler found our @RandomInit annotations.
One of the most important thing building groovy custom annotation is to select correct Compile Phase. There are no out of the box solutions. Which phase is suited best depends on what we want to do with the AST transformation. We use “Semantic Analysis” and it is the earliest phase we can process our custom annotation.
Groovy AST Browser may help you to check what happens with code during each phase and which one is best for your annotation.

**Usage example:**

```
package test

import com.illerax.randominit.RandomInit

class RandomInitTest extends GroovyTestCase {
    void testRandomInitAnnotation() {

        @RandomInit def exp
        assert exp == null

        @RandomInit Integer int1
        assert int1 != null

        @RandomInit(min = -10, max = 10) Integer int2
        assert int2 >= -10
        assert int2 <= 10

        @RandomInit(min = -100, max = -50) Integer int3
        assert int3 >= -100
        assert int3 <= -50

        @RandomInit(min = 40, max = 100) BigDecimal dec1
        assert dec1 >= 40
        assert dec1 <= 100

        @RandomInit(min = 1, max = 1) BigDecimal dec2
        assert dec2 == 1

        @RandomInit(minLength = 3, maxLength = 3) String str1
        str1.size() == 3
    }
}
```

**Useful Links:**

https://github.com/illerax/random_init – @RandomInit sources

http://groovy-lang.org/objectorientation.html#_annotation – Official Documentation

http://melix.github.io/ast-workshop/ – Groovy AST Transformations Workshop by Cédric Champeau

http://glaforge.appspot.com/article/groovy-ast-transformations-tutorials – Articles recommended by Guillaume Laforge
