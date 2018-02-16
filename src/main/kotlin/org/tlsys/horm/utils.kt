package org.tlsys.horm

import java.sql.Connection
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