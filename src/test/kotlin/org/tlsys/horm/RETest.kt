package org.tlsys.horm

import org.h2.jdbcx.JdbcDataSource
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import java.sql.Connection
import java.util.*
import javax.persistence.*
import javax.sql.DataSource

@Entity
@Table(name = "test")
class TestClass(
        @Column(nullable = false)
        val field: String = ""
) {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id = 0L
}

fun DataSource.sizeOf(tableName: String, where: String = "") = connection.use {
    it.createStatement().use {
        it.executeQuery("select count(*) from $tableName${if (where.isNotBlank()) " where $where" else ""}").let {
            if (it.next())
                it.getLong(1)
            else
                0L
        }
    }
}


class RETest {

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
    fun `Rollback при исключении`() {
        try {
            db.re {
                it.persist(TestClass("OLOLO"))
                it.flush()
                throw RuntimeException()
            }
        } catch (e: Throwable) {
        }

        ds.sizeOf("test").eq(0)
    }

    @Test
    fun `Просто запись`() {
        db.re {
            it.persist(TestClass("OLOLO"))
        }
        ds.sizeOf("test").eq(1)
    }

    @Test
    fun `Отдельная транзакция`() {
        db.re {

            try {
                db.tx {
                    it.persist(TestClass("OLOLO"))
                    throw RuntimeException()
                }
            } catch (e: Throwable) {
            }

            it.persist(TestClass("OLOLO"))
            it.flush()
        }
        ds.sizeOf("test").eq(1)
    }


    @Test
    fun `Rollback при вложенной функции`() {
        try {
            db.re {
                println("start")
                try {
                    db.re {
                        it.persist(TestClass("OLOLO"))
                        throw RuntimeException()
                    }
                } catch (e: Throwable) {

                }

                it.persist(TestClass("OLOLO"))
                it.flush()
            }
        } catch (e: Throwable) {
        }

        ds.sizeOf("test").eq(0)
    }

    @Test
    fun `Rollback при вложенной функции1`() {
        try {
            db.re {
                println("start")
                try {
                    db.su {
                        it.persist(TestClass("OLOLO"))
                        throw RuntimeException()
                    }
                } catch (e: Throwable) {

                }

                it.persist(TestClass("OLOLO"))
                it.flush()
            }
        } catch (e: Throwable) {
        }

        ds.sizeOf("test").eq(0)
    }
}