# Horm

### How to use


First, you need to add a repository, as well as a dependency in to your **build.gradle** file:
```groovy
repositories {
    mavenCentral()
    mavenLocal()
    maven {
        url 'http://tlsys.org/repo/'
    }
}

dependencies {
    compile "org.tlsys:horm:1.0"
}
```