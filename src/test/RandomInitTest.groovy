package test

import com.illerax.randominit.RandomInit

/**
 * Created by Evgeny Smirnov on 01/03/17.
 */
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
