# 框架结构

[toc]

## 项目配置

[kapt+kotlinPoet配置](https://github.com/Chen-Xi-g/Module_Study/blob/master/kotlinPoetConfig.md)

## app（壳工程）

> 壳工程，主要用来测试

## login（业务组件）

> 登陆的业务组件

## common（业务组件）

> 通用配置

## GRoute（Kotlin 路由框架）

### groute_annotation（路由组件的注解）

> 这里存放着路由框架的注解和路由框架的实体类

**package: com.minlukj.annotation**

**Annotation: @GRouter（这是一个注解类，注意使用该注解注册路由表。）**

```kotlin
// 哪里可以使用该注解
@Target(AnnotationTarget.CLASS)
// 注解在编译时使用
@Retention(AnnotationRetention.RUNTIME)
/**
 * @param group 传入组名称
 * @param path 传入组下Activity路径名称，这里必须以 "/" 开头，根据阿里路由源码定义。
 * 列：  path = "/app/MainActivity" or path = "/login/Login_MainActivity"
 */
annotation class GRouter constructor(val group: String = "", val path : String)
```

**package: com.minlukj.annotation.bean**

**Class: GRouterBean（路由组件的实体类）** 

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

**Class: TypeEnum（页面的枚举类）**

```kotlin
/**
 * 路由实体类的枚举扩展类，目前只有Activity，使用枚举是为了后期的扩展
 */
enum class TypeEnum{
    ACTIVITY
}
```

---

### groute_api（路由组件的API）

> 这里存放着路由框架定义的接口，后续扩展的接口需要卸载这里

**package: com.minlukj.groute_api**

**Interface: GRouterGroup（路由框架的分组接口）**

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

**Interface: GRouterPath（路由框架的组件路径接口）**

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

**Class: GBundleManager（跳转时 Intent 传递参数）**

```kotlin
/**
 * 跳转时 Intent ，用于参数的传递
 */
public class BundleManager {
    // 对外界提供，可以携带参数的方法
    public BundleManager withString(@NonNull String key, @Nullable String value) {
        bundle.putString(key, value);
        return this;
    }
    //...
    // 直接完成跳转
    public Object navigation(Context context) {
        // 单一原则
        // 把自己所有行为 都交给了  路由管理器
        return RouterManager.getInstance().navigation(context, this);
    }
}
```

**Class: GRouterManager（路由管理器，辅助完成交互通信）**

```kotlin
/*
 *  路由管理器，辅助完成交互通信
 *  详细流程：
 *      1.拼接 找 GRouter$$Group$$login
 *      2.进入 GRouter$$Group$$login 调用函数返回groupMap
 *      3.执行 groupMap.get(group)  group == login
 *      4.查找  GRouter$$Path$$login.class
 *      5.进入  GRouter$$Path$$login.class 执行函数
 *      6.执行 pathMap.get(path) path = "/login/Login_MainActivity"
 *      7.拿到 RouterBean（login_MainActivity.class）
 *      8.startActivity（new Intent(this, Login_MainActivity.class)）
 */
class GRouterManager private constructor() {

    /**
     * 单例
     * 双重校验锁模式
     */
    companion object {
        val instance: GRouterManager by lazy(mode = LazyThreadSafetyMode.SYNCHRONIZED) {
            GRouterManager()
        }
    }

    /**
     * 这两个成员参数的意义：
     *      group : 根据组名可以拿到 KAPT 自动生成的 `GRouter$$Path$$login`
     *      path : 根据路径可以拿到对应的 Class 类 Login_MainActivity.class
     */
    private var group = ""
    private var path = ""

    /**
     * 为了保证性能，使用 LruCache 缓存
     */
    private val groupLruCache = LruCache<String, GRouterGroup>(100)
    private val pathLruCache = LruCache<String, GRouterPath>(100)

    // 方便拼接 FILE_GROUP_NAME + group
    private val FILE_GROUP_NAME = "GRouter$\$Group$\$"

    /**
     * 要跳转到哪个Activity，传入要跳转的Activity路由path校验
     */
    fun build(path: String): GBundleManager {
        // 1. 判断Path是否符合规则 否则提示异常
        // ...

        // 2. 符合规则为 group 和 path 赋值

        return GBundleManager()
    }

    /**
     * 在这里完成跳转的逻辑
     * @param context 传入context完成跳转
     * @param bundle 传入bundle完成参数传递
     */
    fun navigation(context: Context, bundle: GBundleManager): Any? {
        // 1. 拼接 Group 绝对路径 用来查找 KAPT 在指定 Module 生成的文件
        // ...
        
        // 2. 从Group缓存中读取 GRouterGroup
        // ...

        // 3. 从Path缓存中读取 GRouterPath
        // ...

        // 4. 判断枚举类型是否为Activity
        when (gRouterBean?.typeEnum) {
            //可以通过自己扩展的类型进行判断
            TypeEnum.ACTIVITY -> {
                //在这里完成Intent跳转传值
            }
            //...
        }

        return null
    }

}
```



---

### groute_compiler（路由组件的KAPT）

> 这里存放着路由框架的核心代码，kapt和kotlinPoet都是在这里工作。

**package: com.minlukj.compiler.util**

**Class: GRouterConfig（路由组件的通用配置）**

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

**Class: GRouterProcessor（注解处理器）** 

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

### 如何使用

1. 添加Activity到路由表：在Activity上添加注解`@GRouter(path = "")`，这里的 path 格式必须是 `/app/MainActivity` 、`/login/Login_MainActivity`、`/xxx/Xxx_YyyActivity`。

2. 从路由表中跳转到指定Activity并传递参数：

   ```kotlin
   // build 中传递要跳转的Activity path
   // withXxxi 传递参数
   // 必须调用 navigation 来跳转页面 否则不会无效。
   GRouterManager.instance
       .build("/app/MainActivity")
       .withString("tag", "/order/Order_MainActivity")
       .navigation(this)
   ```
