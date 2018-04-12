@file:JvmName("TestUtils")

package org.tlsys.horm

import org.junit.Assert

fun <T, V : AutoCloseable> V.use(f: (V) -> T): T {
    try {
        return f(this)
    } finally {
        close()
    }
}

fun <T> T.eq(value: T): T {
    Assert.assertEquals(value, this)
    return this
}