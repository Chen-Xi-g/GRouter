# 框架结构

[toc]
## 项目配置
[kapt+kotlinPoet配置](https://github.com/Chen-Xi-g/Module_Study/blob/master/kotlinPoetConfig.md)

## app

> 壳工程，主要用来测试

## login

> 登陆的业务组件

## common

> 通用配置

## GRoute（Kotlin 路由框架）

### groute_annotation

> 这里存放着路由框架的注解和路由框架的实体类

**package: com.minlukj.annotation**

**Annotation: @GRouter：** 这是一个注解类，注意使用该注解注册路由表。

```kotlin
// 哪里可以使用该注解
@Target(AnnotationTarget.CLASS)
// 注解在编译时使用
@Retention(AnnotationRetention.RUNTIME)
/**
 * @param group 传入组名称
 * @param path 传入组下Activity路径名称，这里必须以 "/" 开头，根据阿里路由源码定义。
 * 列：	path = "/app/MainActivity" or path = "/login/Login_MainActivity"
 */
annotation class GRouter constructor(val group: String = "", val path : String)
```

**package: com.minlukj.annotation.bean**

**Class: GRouterBean** 部分代码

```kotlin
/**
 * 封装路由路径Path的实体类对象，用于初始化和传递参数。
 * 该实体类使用 Builder 模式来实例化
 * @param typeEnum 枚举类型：Activity, 暂时只有Activity，使用枚举为了后期的扩展
 * @param clazz 被注解标注的类 如：MainActivity.kt  ...
 * @param path Activity的路由路径 "/app/MainActivity" or "/login/Login_MainActivity"
 * @param group 路由组名称 "app" or "order" or "login"
 */
data class GRouterBean(
    var typeEnum: TypeEnum?,
    var clazz: Class<*>?,
    var path: String?,
    var group: String?
) {
    lateinit var element: Element
    /**
     * @param element 类节点 可以从这里拿到类的很多信息
     */
    constructor(
        typeEnum: TypeEnum?,
        clazz: Class<*>?,
        path: String?,
        group: String?,
        element: Element?//这里需要注意 是javax.lang.model.element包下的Element
    ):this(typeEnum,clazz,path,group){
        if (element != null) {
            this.element = element
        }
    }

	// ...
	companion object{
        //提供对外暴露的方法，初始化路由框架的实体类
        fun create(typeEnum: TypeEnum,clazz: Class<*>,path: String,group: String): GRouterBean{
            return GRouterBean(typeEnum,clazz,path,group)
        }
    }
    class Builder{
    	// ...
    	fun build(): GRouterBean{
            if (path?.isEmpty()!!)
                throw RuntimeException("必填项path为空")
            return GRouterBean(typeEnum,clazz,path,group,element)
        }
    }
	// ...
}
```

**Class: TypeEnum**

```kotlin
/**
 * 路由实体类的枚举扩展类，目前只有Activity，使用枚举是为了后期的扩展
 */
enum class TypeEnum{
    ACTIVITY
}
```



### groute_api

> 这里存放着路由框架定义的接口，后续扩展的接口需要卸载这里

**package: com.minlukj.groute_api**

**Interface: GRouterGroup**

```kotlin
interface GRouterGroup {
    /**
     * 每个组件都是一个组，每个组下都会有该组件的Activity。
     *      列如：login组件下有 LoginMainActivity 这个组下有很多通过注解设置的path ："login/login_main_activity"
     * 所以我们需要设计一个Map来存储这些信息
     */
    fun getGroupMap(): Map<String,Class<out GRouterPath>>
}
```

**Interface: GRouterPath**

```kotlin
interface GRouterPath {
    /**
     * 这里存放着每个组件下所有的Activity路径
     *      列入：login组件下所有路由实体类信息。
     *          --> GRouteBean(login_main_activity)
     *          --> GRouteBean(login_main_activity2)
     *          --> .......
     * 所以需要设计一个Map来存储这些信息
     */
    fun getPathMap():Map<String,GRouterBean>
}
```

### groute_compiler

> 这里存放着路由框架的核心代码，kapt和kotlinPoet都是在这里工作。

**package: com.minlukj.compiler.util**

**Class: GRouterConfig**

```kotlin
/**
 * GRouter 框架的常量配置
 */
class GRouterConfig {
    companion object{
        //@GRouter 注解的包名+类名
        const val GROUTER_PACKAGE = "com.minlukj.annotation.GRoute"

        //接收每个module的Tag标记
        const val OPTIONS = "moduleName"//Module的名称
        const val APT_PACKAGE = "packageNameForAPT"//APT 存放的包名
        // ...
    }
}
```

**package:com.minlukj.compiler**

**Class: GRouterProcessor** 这个类是主要工作的地方，详情查看Class注释

```kotlin
@AutoService(Processor::class)// 编译期 绑定 干活
@SupportedOptions(GRouterConfig.OPTIONS,GRouterConfig.APT_PACKAGE)//接收参数的值
@SupportedSourceVersion(SourceVersion.RELEASE_8)// 指定source版本，最新的就可以
//@SupportedAnnotationTypes(GRouterConfig.GROUTER_PACKAGE) //这里设置需要监听的注解，通过注解或重写都可以
class GRouterProcessor : AbstractProcessor() { //必须继承 AbstractProcessor

	// 这是四个常用的工具
    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private lateinit var elementTool: Elements

    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private lateinit var typeTool: Types

    // Message用来打印 日志相关信息  == Log.i
    private lateinit var messager // Gradle 日志中输出
            : Messager

    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private lateinit var filer: Filer

    // 通过重写指定Source版本
//    override fun getSupportedSourceVersion(): SourceVersion {
//        return SourceVersion.latestSupported()
//    }
	// 通过重写指定要监控的注解
    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val types = LinkedHashSet<String>()
        types.add(GRouter::class.java.canonicalName)
        return types
    }
    //在这里初始化常用工具和接收定义的值
    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elementTool = processingEnv.elementUtils
        typeTool = processingEnv.typeUtils
        messager = processingEnv.messager
        filer = processingEnv.filer

        // 接收配置的数据，如果没有接收到则失败
        options = processingEnv.options[GRouterConfig.OPTIONS].toString()
        aptPackage = processingEnv.options[GRouterConfig.APT_PACKAGE].toString()

        if (options.isEmpty() && aptPackage.isEmpty()){
            messager.printMessage(Diagnostic.Kind.ERROR,"APT 环境搭建失败，请检查 options 和 aptPackage")
        }else{
            messager.printMessage(Diagnostic.Kind.NOTE,"GRouter 环境搭建成功!!!")
        }
    }

    //这个方法是必须重写的，所有的工作都在这里执行
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
    	// ...
    }
}
```

