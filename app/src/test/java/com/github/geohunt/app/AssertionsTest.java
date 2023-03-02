package com.github.geohunt.app;

import static org.junit.Assert.fail;

import com.github.geohunt.app.utility.Assertions;

import org.junit.Assert;
import org.junit.Test;

public class AssertionsTest {
    @Test
    public void assertNonNullFailedWhenNull() {
        Assert.assertThrows(AssertionError.class, () -> {
            Assertions.assertNonNull(null);
        });
    }

    @Test
    public void assertNonNullSucceedWhenNonNull() {
        try {
            Assertions.assertNonNull(new Object());
        }
        catch (Exception e) {
            fail();
        }
    }
}
