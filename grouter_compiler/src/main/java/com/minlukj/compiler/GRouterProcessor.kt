package com.minlukj.compiler

import com.google.auto.service.AutoService
import com.minlukj.annotation.GRouter
import com.minlukj.annotation.bean.GRouterBean
import com.minlukj.annotation.bean.TypeEnum
import com.minlukj.compiler.util.GRouterConfig
import com.squareup.kotlinpoet.*
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic


/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.compiler
 * @ClassName:      GRouteProcessor
 * @Description:    kotlin 自动生成代码
 * @Author:         忞鹿
 * @CreateDate:     2020/9/7 10:41
 */
@AutoService(Processor::class)// 编译期 绑定 干活
@SupportedOptions(GRouterConfig.OPTIONS, GRouterConfig.APT_PACKAGE)//接收参数的值
@SupportedSourceVersion(SourceVersion.RELEASE_8)
//@SupportedAnnotationTypes(GRouterConfig.GROUTER_PACKAGE)
class GRouterProcessor : AbstractProcessor() {

    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private lateinit var elementTool: Elements

    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private lateinit var typeTool: Types

    // Message用来打印 日志相关信息  == Log.i
    private lateinit var messager // Gradle 日志中输出
            : Messager

    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private lateinit var filer: Filer

    private lateinit var options: String // （模块传递过来的）模块名
    private lateinit var aptPackage: String // （模块传递过来的） 包名

    // 仓库一  PATH
    private val mAllPathMap = mutableMapOf<String, ArrayList<GRouterBean>>()

    // 仓库二 GROUP
    private val mAllGroupMap = mutableMapOf<String, String>()

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
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>$options")
        messager.printMessage(Diagnostic.Kind.NOTE, ">>>>>>>>>>$aptPackage")

        if (options.isEmpty() && aptPackage.isEmpty()) {
            messager.printMessage(Diagnostic.Kind.ERROR, "APT 环境搭建失败，请检查 options 和 aptPackage")
        } else {
            messager.printMessage(Diagnostic.Kind.NOTE, "GRouter 环境搭建成功!!!")
        }

    }

    //这个方法是必须重写的，所有的工作都在这里执行
    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {

        //判断被注解标注的类有几个，如果为空则返回false 不处理
        if (annotations.isNullOrEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "没有找到被 @GRouter 标注的类")
            return false
        }

//        使用ElementTool获取Activity 的 TypeMirror
        val activityType = elementTool.getTypeElement(GRouterConfig.ACTIVITY_PACKAGE)
        val activityMirror = activityType?.asType()

        //获取所有被GRouter标注的类
        val elementsAnnotatedWith = roundEnv.getElementsAnnotatedWith(GRouter::class.java)
        messager.printMessage(
            Diagnostic.Kind.NOTE,
            "$>>>>>>>>>>>>>>>>>>>>>>${elementsAnnotatedWith.size}"
        )
        //如果被 @GRouter 标注那么可以生成类
        elementsAnnotatedWith?.forEach {
            //返回这个Element所在的包名称
            val packName = elementTool.getPackageOf(it)?.qualifiedName.toString()

            //获取类名
            val className = it.simpleName.toString()
            messager.printMessage(Diagnostic.Kind.NOTE, "被@GRouter注解的类有：$className")

            //拿到注解
            val gRouter = it.getAnnotation(GRouter::class.java)

//            初始化GRouter实体类
            val routerBean = GRouterBean.Builder()
                .addGroup(gRouter.group)
                .addPath(gRouter.path)
                .addElement(it)
                .build()

            //判断当前类是否为Activity
            val itMirror = it.asType()
            //测试一种类型是否是另一种的子类型。
            //任何类型都被视为自身的子类型。
            if (typeTool.isSubtype(itMirror, activityMirror))
                routerBean.typeEnum = TypeEnum.ACTIVITY
            else
                messager.printMessage(Diagnostic.Kind.ERROR, "@GRouter注解目前仅限适用于Activity类之上")

            //校验注解@GRouter Path 设置的 Group 是否和 options 相同
            if (checkRouterPath(routerBean)) {
                messager.printMessage(Diagnostic.Kind.NOTE, "RouterBean Check Success:$routerBean")
                //获取 PATH 仓库
                var routerList = mAllPathMap[routerBean.group]
                //判断list是否为空,如果为空则初始化
                if (routerList.isNullOrEmpty()) {
                    routerList = arrayListOf(routerBean)
                    mAllPathMap[routerBean.group!!] = routerList
                } else { // 如果list不为空，说明该Group中有path仓库
                    routerList.add(routerBean)
                }
            } else {
                messager.printMessage(
                    Diagnostic.Kind.ERROR,
                    "@ARouter注解未按规范配置，如：/app/MainActivity"
                );
            }
        }

        //拿到Group 和 Path 的 Element
        val pathElement = elementTool.getTypeElement(GRouterConfig.GROUTER_API_PATH)
        val groupElement = elementTool.getTypeElement(GRouterConfig.GROUTER_API_GROUP)

//        用KotlinPoet生成 Path 文件
        createPathFile(pathElement)
        createGroupFile(groupElement, pathElement)

        return true
    }

    // 自动生成Path的代码
    private fun createPathFile(pathElement: TypeElement) {
        /**
         * KotlinPoet 自动生成Path代码的模板
         *
         * class `GRouter$$Path$$app` : GRouterPath {
         *      override fun getPathMap(): Map<String, GRouterBean> {
         *          val pathMap = mutableMapOf<String,GRouterBean>()
         *          pathMap["/app/MainActivity"] = GRouterBean.create(TypeEnum.ACTIVITY,MainActivity::class.java,"/app/MainActivity","app")
         *          return pathMap
         *      }
         * }
         *
         */

        // 先判断 path map是否为空
        if (mAllPathMap.isEmpty())
            return

        //设置Map泛型的TypeName
        val map = ClassName("kotlin.collections", "Map")
        val string = ClassName("kotlin", "String")
        val mapOfGRouterBean = map.parameterizedBy(string, GRouterBean::class.asTypeName())

        mAllPathMap.forEach {
            //初始化map的函数

            // 初始化方法的返回值
            val statement = FunSpec.builder(GRouterConfig.PATH_METHOD_NAME)
                .addModifiers(KModifier.OVERRIDE)
                .returns(mapOfGRouterBean)
                .addStatement(
                    "val ${GRouterConfig.PATH_VAR} = %M<%T,%T>()",
                    MemberName("kotlin.collections", "mutableMapOf"),
                    String::class.asClassName(),
                    GRouterBean::class.asTypeName()
                )
            it.value.forEach { bean ->
//                pathMap["/app/MainActivity"] = GRouterBean.create(TypeEnum.ACTIVITY,MainActivity::class.java,"/app/MainActivity","app")
                statement.addStatement(
                    "${GRouterConfig.PATH_VAR}[%S] = %T.create(%T.%L, %T::class.java, %S, %S)",
                    bean.path!!, //    s
                    GRouterBean::class,   // T
                    TypeEnum::class,
                    bean.typeEnum!!,
                    bean.element as TypeElement,
                    bean.path!!,
                    bean.group!!
                )
            }
            statement.addStatement(
                "return ${GRouterConfig.PATH_VAR}"
            )
            val finalClassName = GRouterConfig.PATH_FILE_NAME + it.key
            val file = FileSpec.builder(aptPackage, finalClassName)
                .addType(
                    TypeSpec.classBuilder(finalClassName)
                        .addSuperinterface(
                            ClassName(
                                GRouterConfig.GROUTER_API_PACKAGE,
                                pathElement.simpleName.toString()
                            )
                        )
                        .addFunction(statement.build())
                        .build()
                )
                .build()
            file.writeTo(filer)
            mAllGroupMap[it.key] = finalClassName
        }

    }

    private fun createGroupFile(groupType: TypeElement, pathType: TypeElement) {
//        判断GroupMap和GroupMap-->PathMap是否为空，如果为空说明不需要自动生成
        if (mAllGroupMap.isNullOrEmpty() || mAllPathMap.isNullOrEmpty()) {
            messager.printMessage(Diagnostic.Kind.NOTE, "Group暂时不需要生成")
            return
        }

        /**
         * KotlinPoet 自动生成Group代码的模板
         *
         * class `GRouter$$Group$$app`: GRouterGroup {
         *  override fun getGroupMap(): Map<String, Class<out GRouterPath>> {
         *      val groupMap = mutableMapOf<String,Class<out GRouterPath>>()
         *      groupMap["app"] = `GRouter$$Path$$app`::class.java
         *      return groupMap
         *  }
         * }
         */

        //先初始化需要的TypeName
        val map = ClassName("kotlin.collections", "Map")
        val string = ClassName("kotlin", "String")
        val pathClassName =
            ClassName(GRouterConfig.GROUTER_API_PACKAGE, pathType.simpleName.toString())
        val groupClassName =
            ClassName(GRouterConfig.GROUTER_API_PACKAGE, groupType.simpleName.toString())
        val returns = map.parameterizedBy(
            string,
            Class::class.asClassName().parameterizedBy(WildcardTypeName.producerOf(pathClassName))
        )

        //GroupClass 名称
        val finalClassName = GRouterConfig.GROUP_FILE_NAME + options

//先创建函数
        val funSpec = FunSpec.builder(GRouterConfig.GROUP_METHOD_NAME)
            .addModifiers(KModifier.OVERRIDE)
            .returns(returns)
            .addStatement(
                "val ${GRouterConfig.GROUP_VAR} = %M<%T,%T>()",
                MemberName("kotlin.collections", "mutableMapOf"),
                String::class.asClassName(),
                Class::class.asClassName()
                    .parameterizedBy(WildcardTypeName.producerOf(pathClassName))
            )
        mAllGroupMap.forEach {
//            Group Path 名称
            funSpec.addStatement(
                "${GRouterConfig.GROUP_VAR}[%S] = %T::class.java",
                it.key,
                ClassName(aptPackage, it.value)
            )
        }
        funSpec.addStatement("return ${GRouterConfig.GROUP_VAR}")
        val file = FileSpec.builder(aptPackage, finalClassName)
            .addType(
                TypeSpec.classBuilder(finalClassName)
                    .addSuperinterface(groupClassName)
                    .addFunction(
                        funSpec.build()
                    )
                    .build()
            ).build()

        file.writeTo(filer)
    }

    private fun checkRouterPath(bean: GRouterBean): Boolean {
        val group = bean.group
        val path = bean.path

        // 从阿里路由源码中可以看到，Path的值必须以 "/" 开头，这里也使用同样的规则
        // 这里不推荐使用startsWith，因为 Path 可能写为 "//" 开头
        if (path!!.lastIndexOf("/") == 0) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@GRouter注解未按规范配置，如：/app/MainActivity")
            return false
        }

        //截取 Path 中 Group 的值
        val finalGroup = path.substring(1, path.indexOf("/", 1))

        //判断组件中的group名称是否和注解中的group是否相同，这里要统一配置规范
        if (group!!.isNotEmpty() && group != options) {
            messager.printMessage(Diagnostic.Kind.ERROR, "@GRouter注解中的group值必须和子模块名一致！")
        } else {
            bean.group = finalGroup
        }

        return true
    }


}