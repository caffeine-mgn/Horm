package org.tlsys.horm

import org.h2.jdbcx.JdbcDataSource
import org.junit.Before
import org.junit.Test
import java.util.*

class CacheTest {
    lateinit var ds: JdbcDataSource
    lateinit var db: Horm

    @Before
    fun before() {
        ds = JdbcDataSource()
        ds.setURL("jdbc:h2:mem:${UUID.randomUUID()};DB_CLOSE_DELAY=-1")

        db = HormDataSourceConfig(ds) {
            it.classes += TestClass::class.java
            it.mode = HibernateConfig.CreateMode.Update
        }.build()
    }

    @Test
    fun test() {
        ds.sizeOf("test").eq(0)
        db.re {
            it.persist(TestClass("OLOLO"))
        }
        ds.connection.use {
            it.prepareStatement("insert into test values(?,?)").apply {
                setLong(1, 0)
                setString(2, "")
                executeUpdate()
            }
            it.commit()
        }
        db.re {
            it.createQuery("select count(r) from TestClass r", java.lang.Long::class.java).singleResultOrNull ?: 0L
        }.eq(2L)

        ds.sizeOf("test").eq(2)
    }
}