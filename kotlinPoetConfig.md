# KAPT + KotlinPoet 配置

[toc]

> 使用kotlin的kapt注解处理器加`kotlinPoet`自动生成代码

## 1. 创建Library

> 在根目录创建两个`Java or Kotlin Library`,分别为: annotation | compiler, annotation存放注解,compiler存放AbstractProcessor

## 2. 引入KAPT、AutoService、KotlinPoet

1. 引入KAPT: 在compiler和app工程的`build.gradle`添加`apply plugin: 'kotlin-kapt'`

2. 引入AutoService: 到github查看最新的 [AutoService](https://github.com/google/auto) 版本添加以下代码.

   ```groovy
   dependencies {
       //...
   	compileOnly 'com.google.auto.service:auto-service:[version]'
   	kapt 'com.google.auto.service:auto-service:[version]'
       //...
   }
   ```

3. 引入KotlinPoet: 到github查看最新的 [KotlinPoet](https://github.com/square/kotlinpoet/tags) 版本,添加以下代码.

   ```groovy
   dependencies{
   	//...
       implementation "com.squareup:kotlinpoet:1.6.0"
       //...
   }
   ```

4. 引入`annotation library`

   ```groovy
   dependencies{
   	//...
   	implementation project(':annotation')
   	//...
   }
   ```

## 3. 在build.gradle中添加传值参数

> kotlin 和 java 传递参数是有区别的，列@SupportedOptions设置接收的参数为`moduleName`和`packageNameForAPT`

**kotlin:**

```groovy
apply plugin: 'kotlin-kapt'

kapt{
    arguments{
        arg("moduleName", project.name)
        arg("packageNameForAPT", this.getRootProject().ext.packageNameForAPT)
    }
}
```

**java:**

```groovy
android{
	defaultConfig{
		javaCompileOptions {
            annotationProcessorOptions {
                arguments = [moduleName: project.getName(), packageNameForAPT: packageNameForAPT]
            }
        }
	}
}
```



## 4. 创建 AbstractProcessor, 使用kotlinPoet生成代码, 生成规则查看[官方API](https://square.github.io/kotlinpoet/)

> 在 compiler library 包下创建一个继承AbstractProcessor的类

​

```kotlin
/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.compiler
 * @ClassName:      GRouteProcessor
 * @Description:    Java 自动生成代码
 * @Author:         忞鹿
 * @CreateDate:     2020/9/7 10:41
 */
@AutoService(Processor::class)// 编译期 绑定 干活
@SupportedOptions("moduleName","packageNameForAPT")//接收参数的值
@SupportedAnnotationTypes("com.minlukj.annotation.Groute")//注解路径
@SupportedSourceVersion(SourceVersion.RELEASE_8)//版本
class GRouteProcessor : AbstractProcessor() {

    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private var elementTool: Elements? = null

    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private var typeTool: Types? = null

    // Message用来打印 日志相关信息  == Log.i
    private var messager // Gradle 日志中输出
            : Messager? = null

    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private var filer: Filer? = null

    // 使用注解或重写都可以
//    override fun getSupportedSourceVersion(): SourceVersion {
//        return SourceVersion.latestSupported()
//    }

//    override fun getSupportedAnnotationTypes(): MutableSet<String> {
//        val types = LinkedHashSet<String>()
//        types.add(GRoute::class.java.canonicalName)
//        return types
//    }

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elementTool = processingEnv.elementUtils
        typeTool = processingEnv.typeUtils
        messager = processingEnv.messager
        filer = processingEnv.filer
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
    	return false
    }
}
```



1. 需要使用的工程需要用kapt引入,在app工程引入compiler和annotation
2. 每次Make Project都会生成代码,路径:`generated/soure/kapt/debug/包名/xxxx.kt`

**注: 在AbstractProcessor中不可以使用ERROR打印日志, 否则会报错, 只有在特殊情况下使用.**