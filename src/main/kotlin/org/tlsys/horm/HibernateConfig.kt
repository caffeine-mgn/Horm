package org.tlsys.horm

interface HibernateConfig {
    var dialect: String?
    val classes: MutableList<Class<*>>
    var schema: String?
    var mode: CreateMode
    var fetchSize: Int?
    var classLoader: ClassLoader?
    var showSQL: Boolean
    var xmlConfig: String?


    enum class CreateMode(val property: String) {
        None("none"),
        Validate("validate"),
        Update("update"),
        Create("create"),
        CreateDrop("create-drop")
    }
}