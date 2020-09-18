package com.minlukj.compiler

import com.google.auto.service.AutoService
import com.minlukj.annotation.GParameter
import com.minlukj.compiler.factory.GParameterFactory
import com.minlukj.compiler.util.GRouterConfig
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeSpec
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types
import javax.tools.Diagnostic

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.compiler
 * @ClassName:      GParameterProcessor
 * @Description:     kotlin类作用描述
 * @Author:         忞鹿
 * @CreateDate:     2020/9/18 10:41
 */
@AutoService(Processor::class)
@SupportedSourceVersion(SourceVersion.RELEASE_8)
class GParameterProcessor : AbstractProcessor() {
    // 操作Element的工具类（类，函数，属性，其实都是Element）
    private lateinit var elementTool: Elements

    // type(类信息)的工具类，包含用于操作TypeMirror的工具方法
    private lateinit var typeTool: Types

    // Message用来打印 日志相关信息  == Log.i
    private lateinit var messager // Gradle 日志中输出
            : Messager

    // 文件生成器， 类 资源 等，就是最终要生成的文件 是需要Filer来完成的
    private lateinit var filer: Filer

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        val types = LinkedHashSet<String>()
        types.add(GParameter::class.java.canonicalName)
        return types
    }

    // 临时map存储，用来存放被@Parameter注解的属性集合，生成类文件时遍历
    // key:类节点, value:被@Parameter注解的属性集合
    private val tempParameterMap = HashMap<TypeElement, ArrayList<Element>>()

    override fun init(processingEnv: ProcessingEnvironment) {
        super.init(processingEnv)
        elementTool = processingEnv.elementUtils

        typeTool = processingEnv.typeUtils

        messager = processingEnv.messager

        filer = processingEnv.filer
        messager.printMessage(Diagnostic.Kind.NOTE, "初始化Parameter成功")
    }

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment
    ): Boolean {
        messager.printMessage(Diagnostic.Kind.NOTE, "是否为空：${annotations.isNullOrEmpty()}")
        if (!annotations.isNullOrEmpty()) {

            // TODO 获取那些地方使用了 @Parameter 注解
            val elements = roundEnv.getElementsAnnotatedWith(GParameter::class.java)
            if (elements.isEmpty()) return false
            for (element in elements) {

                // 获取类节点
                val enclosingElement = element.enclosingElement as TypeElement
                messager.printMessage(Diagnostic.Kind.NOTE, "类节点：$enclosingElement")
                messager.printMessage(Diagnostic.Kind.NOTE, "类节点：$element")
                // 是否存在 如果存在直接添加
                if (tempParameterMap.containsKey(enclosingElement)) {
                    tempParameterMap[enclosingElement]?.add(element)
                } else { //不存在创建后添加
                    val list = arrayListOf<Element>()
                    list.add(element)
                    tempParameterMap[enclosingElement] = list
                }
            }

            // TODO 使用KAPT生成类文件
            //判断是否需要生成类文件
            if (tempParameterMap.isEmpty()) return false

            //获取Activity 和 Parameter 类型
            val activityElement = elementTool.getTypeElement(GRouterConfig.ACTIVITY_PACKAGE)
            val parameterElement = elementTool.getTypeElement(GRouterConfig.GROUTER_API_PARAMETER)

            /**
             * 代码模板
             *
             * class `Order_MainActivity$$Parameter` : GParameterGet {
             *      fun getParameter(targetParameter: Any) {
             *          val t: Order_MainActivity = targetParameter as Order_MainActivity
             *          t.name = t.getIntent().getStringExtra("name")
             *      }
             * }
             */
            val parameterSpec =
                ParameterSpec.builder(GRouterConfig.PARAMETER_VAR, Any::class).build()
            val interfaceName =
                ClassName(GRouterConfig.GROUTER_API_PACKAGE, parameterElement.simpleName.toString())
            //遍历仓库
            tempParameterMap.entries.forEach {
                //获取所有的Activity
                // key == Order_MainActivity
                // value = [tag,name,xxx]
                val typeElement = it.key

                //判断获取参数的类是否为Activity
                if (!typeTool.isSubtype(typeElement.asType(), activityElement.asType())) {
                    throw RuntimeException("@GParameter 当前只支持在 Activity 类使用")
                }

                // 获取当前 Map Key 的类名
//                val className = typeElement.javaClass.asClassName()
                var packageName = typeElement.qualifiedName.toString()
                packageName = packageName.substring(0, packageName.lastIndexOf("."))
                messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "包名：$packageName---------${packageName}"
                )
                val className = ClassName(packageName, typeElement.simpleName.toString())
                // TODO 生成的类函数都在这个工厂类中
                val factory = GParameterFactory.Builder(parameterSpec)
                    .className(className)
                    .messager(messager)
                    .build()
                // 添加代码 val t: Order_MainActivity = targetParameter as Order_MainActivity
                factory.addFirstStatement()
                //添加多行代码对参数赋值 t.name = t.getIntent().getStringExtra("name")
                it.value.forEach { element ->
                    factory.buildStatement(element)
                }
                // 生成类文件的类名拼接
                val finalClassName =
                    "${typeElement.simpleName}${GRouterConfig.PARAMETER_SUFFIX_NAME}"
                // 打印生成的类名
                messager.printMessage(
                    Diagnostic.Kind.NOTE,
                    "KAPT生成获取参数的类文件：${className.packageName}.$finalClassName"
                )
                /**
                 * 生成类模板
                 * class `Order_MainActivity$$Parameter` : GParameterGet {
                 *      factory.build()
                 * }
                 */
                FileSpec.builder(className.packageName, finalClassName)
                    .addType(
                        TypeSpec.classBuilder(finalClassName)
                            .addSuperinterface(interfaceName)
                            .addFunction(factory.build())
                            .build()
                    ).build()
                    .writeTo(filer)
            }
        }
        return true
    }

    /**
     * 这里不建议使用这个方式校验，基础数据类型无法转换为Kotlin
     */
    fun Element.toTypeElementOrNull(): TypeElement? {
        if (this !is TypeElement) {
            processingEnv.messager.printMessage(Diagnostic.Kind.ERROR, "无效的元素类型，期望的类", this)
            return null
        }

        return this
    }
}