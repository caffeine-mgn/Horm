package org.tlsys.horm

import org.hibernate.boot.model.naming.ImplicitNamingStrategy
import org.hibernate.boot.model.naming.PhysicalNamingStrategy

interface HibernateConfig {
    var dialect: String?
    val classes: MutableList<Class<*>>
    var schema: String?
    var catalog: String?
    var mode: CreateMode
    var fetchSize: Int?
    var classLoader: ClassLoader?
    var showSQL: Boolean
    var xmlConfig: String?
    var physicalNamingStrategy: PhysicalNamingStrategy?
    var implicitNamingStrategy: ImplicitNamingStrategy?


    enum class CreateMode(val property: String) {
        None("none"),
        Validate("validate"),
        Update("update"),
        Create("create"),
        CreateDrop("create-drop")
    }
}