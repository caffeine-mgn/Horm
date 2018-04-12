package org.tlsys.horm

import org.h2.jdbcx.JdbcDataSource
import org.junit.Test
import java.util.*

class UtilTest {
    @Test
    fun testNotExist() {
        val ds = JdbcDataSource()
        ds.setURL("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1")

        val db = HormDataSourceConfig(ds) {
            it.classes += TestClass::class.java
            it.mode = HibernateConfig.CreateMode.Update
        }.build()

        db.re {
            it.createQuery("select r from TestClass r").singleResultOrNull.eq(null)
        }
    }

    @Test
    fun testExist() {
        val ds = JdbcDataSource()
        ds.setURL("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1")

        val db = HormDataSourceConfig(ds) {
            it.classes += TestClass::class.java
            it.mode = HibernateConfig.CreateMode.Update
        }.build()

        val str = UUID.randomUUID().toString()
        db.re {
            it.persist(TestClass(str))
            it.createQuery("select r from TestClass r", TestClass::class.java).singleResultOrNull!!.let {
                it.field.eq(str)
            }
        }
    }
}