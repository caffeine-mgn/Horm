package org.tlsys.horm

import org.hibernate.Session
import org.hibernate.SessionFactory
import org.hibernate.resource.transaction.spi.TransactionStatus
import java.io.Closeable

class Horm(val factory: SessionFactory) : Closeable {
    override fun close() {
        factory.close()
    }


    private val cur = ThreadLocal<Session?>()
    fun <T> tx(f: (Session) -> T): T {
        val old = cur.get()
        val session = factory.openSession()
        cur.set(session)
        session.beginTransaction()
        try {
            val r = f(session)
            session.transaction.commit()
            return r
        } catch (e: Throwable) {
            session.transaction.rollback()
            throw e
        } finally {
            session.close()
            if (old == null)
                cur.remove()
            else
                cur.set(old)
        }
    }

    fun <T> re(f: (Session) -> T): T {
        if (cur.get() != null && cur.get()!!.transaction.status == TransactionStatus.ACTIVE) {
            try {
                return f(cur.get()!!)
            } catch (e: Throwable) {
                factory.currentSession.transaction.markRollbackOnly()
                throw e
            }
        } else {
            val oldSession = cur.get()
            val session = factory.openSession()
            cur.set(session)
            try {
                session.beginTransaction()
                val r = f(session)
                session.transaction.commit()
                return r
            } catch (e: Throwable) {
                session.transaction.rollback()
                throw e
            } finally {
                session.close()
                if (oldSession == null)
                    cur.remove()
                else
                    cur.set(oldSession)
            }
        }
    }

    fun <T> su(f: (Session) -> T): T {
        if (cur.get() !== null && cur.get()!!.transaction.status == TransactionStatus.ACTIVE) {
            return f(cur.get()!!)
        } else {
            val session = factory.openSession()
            val oldSession = cur.get()
            cur.set(session)
            try {
                return f(session)
            } finally {
                session.close()
                if (oldSession == null)
                    cur.remove()
                else
                    cur.set(oldSession)
            }
        }
    }
}