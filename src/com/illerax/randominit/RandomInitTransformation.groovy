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