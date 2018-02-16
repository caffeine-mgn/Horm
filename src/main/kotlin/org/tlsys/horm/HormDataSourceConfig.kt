package org.tlsys.horm

import org.hibernate.boot.registry.StandardServiceRegistryBuilder
import org.hibernate.cfg.Configuration
import org.hibernate.cfg.Environment
import java.io.ByteArrayInputStream
import java.sql.Connection
import javax.sql.DataSource

class HormDataSourceConfig(val dataSource: DataSource, val f: (HibernateConfig) -> Unit) : HibernateConfig {
    override var xmlConfig: String? = null
    override var showSQL: Boolean = false
    override var classLoader: ClassLoader? = null
    override val classes = ArrayList<Class<*>>()
    override var dialect: String? = null
    override var schema: String? = null
    override var mode = HibernateConfig.CreateMode.Update
    override var fetchSize: Int? = null

    fun build() {
        if (dialect == null) {
            dataSource.use {
                dialect = when (it.metaData!!.databaseProductName) {
                    "MySQL" -> "org.hibernate.dialect.MySQL5Dialect"
                    "PostgreSQL" -> "org.hibernate.dialect.PostgreSQL9Dialect"
                    "H2" -> "org.hibernate.dialect.H2Dialect"
                    else -> TODO("Unknown DB ${it.metaData!!.databaseProductName}")
                }
            }
        }

        val configuration = Configuration()
        configuration.setProperty("hibernate.dialect", dialect)
        configuration.setProperty("hibernate.current_session_context_class", "thread")
        configuration.setProperty("hibernate.show_sql", showSQL.toString())
        configuration.setProperty("hibernate.hbm2ddl.auto", mode.property)
        configuration.setProperty("hibernate.connection.autocommit", "false")

        if (schema != null) {
            configuration.setProperty("hibernate.default_schema", "`${schema}`")
        }

        if (fetchSize !== null)
            configuration.setProperty(Environment.STATEMENT_FETCH_SIZE, "${fetchSize}")

        if (xmlConfig != null) {
            configuration.addInputStream(ByteArrayInputStream(xmlConfig!!.toByteArray()))
        }

        var classLoader = if (classLoader != null || classes.isNotEmpty()) {
            val d = classLoader ?: classes.iterator().next().classLoader
            configuration.properties.put(Environment.CLASSLOADERS, listOf(d))
            d
        } else
            null

        val old = Thread.currentThread().contextClassLoader
        if (classLoader !== null)
            Thread.currentThread().contextClassLoader = classLoader
        val repo = StandardServiceRegistryBuilder().applySettings(configuration.properties)
        repo.applySetting(Environment.DATASOURCE, dataSource)
        val serviceRegistry = repo.build()!!

        val factory = configuration.buildSessionFactory(serviceRegistry)
        Thread.currentThread().contextClassLoader = old
    }
}