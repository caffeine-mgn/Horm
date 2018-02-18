package org.tlsys.horm

import org.hibernate.query.Query
import java.sql.Connection
import javax.persistence.NoResultException
import javax.sql.DataSource

internal fun <V> DataSource.use(f: (Connection) -> V): V {
    var connect: Connection? = null
    try {
        connect = this.connection!!
        return f(connect)
    } finally {
        if (connect != null)
            connect.close()
    }
}

val <T>Query<T>.singleResultOrNull: T?
    get() = try {
        singleResult
    } catch (e: NoResultException) {
        null
    }