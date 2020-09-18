package com.minlukj.compiler.factory

import com.minlukj.annotation.GParameter
import com.minlukj.compiler.util.GRouterConfig
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.ParameterSpec
import javax.annotation.processing.Messager
import javax.lang.model.element.Element
import javax.lang.model.type.TypeKind
import javax.tools.Diagnostic

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.compiler.factory
 * @ClassName:      GParameterFactory
 * @Description:    自动生成代码的工厂类
 * @Author:         忞鹿
 * @CreateDate:     2020/9/18 11:22
 *
 * 方法模板
 *      fun getParameter(targetParameter: Any) {
 *          val t: Order_MainActivity = targetParameter as Order_MainActivity
 *          t.name = t.getIntent().getStringExtra("name")
 *      }
 *
 */
class GParameterFactory constructor(builder: Builder) {
    private val className: ClassName
    private val messager: Messager
    private val funBuilder: FunSpec.Builder

    //初始化
    init {
        this.className = builder.className!!
        this.messager = builder.messager!!
        funBuilder = FunSpec.builder(GRouterConfig.PARAMETER_METHOD_NAME)
            .addModifiers(KModifier.OVERRIDE)
            .addParameter(builder.parameter)

    }

    // 只添加一行代码  val t: Order_MainActivity = targetParameter as Order_MainActivity
    fun addFirstStatement() {
        funBuilder.addStatement(
            "val t = ${GRouterConfig.PARAMETER_VAR} as %T",
            className
        )
    }

    fun build(): FunSpec = funBuilder.build()

    /**
     * 添加多行代码
     * 这里添加参数赋值的代码
     * t.name = t.getIntent().getStringExtra("name")
     * @param element 被注解 @GParameter 标注的元素
     */
    fun buildStatement(element: Element) {
        val typeMirror = element.asType()
        val type = typeMirror.kind.ordinal
        //获取被标注的属性名
        val fieldName = element.simpleName.toString()
        //获取注解中的值
        var annotationValue = element.getAnnotation(GParameter::class.java).name
        val annotation = element.getAnnotation(GParameter::class.java)
        val annotationClass = annotation.annotationClass
        //判断注解的值是否为空，如果为空的话 key 值为字段名 否则为注解中的值
        annotationValue = if (annotationValue.isEmpty()) fieldName else annotationValue
        // 设置属性名称 t.name
        val finalValue = "t.$fieldName"
        // 拼接要生成的代码 t.name = t.getIntent().
        var funContent = "$finalValue = t.intent."
        // 判断获取值的类型
        when (type) {
            TypeKind.INT.ordinal ->
                funContent = "${funContent}getIntExtra(%S)"
            TypeKind.BOOLEAN.ordinal ->
                funContent = "${funContent}getBooleanExtra(%S)"
            else -> {
                if (typeMirror.toString().equals(GRouterConfig.STRING, true)) {
                    funContent = "${funContent}getStringExtra(%S)"
                } else {
                    messager.printMessage(
                        Diagnostic.Kind.ERROR,
                        "typeMirror = ${typeMirror}目前只支持传递 String、Int、Boolean"
                    )
                }
            }
        }
        //为了保证代码的健壮性 判断是否以 ) 结尾
        if (funContent.endsWith(")")) {
            funContent = "$funContent ?: \"\""
            // 添加代码
            // 此时funContent 为 t.name = t.getIntent().getXxxExtra(%S)
            //    annotationValue 为字段名 如： name、tag、xxx
            funBuilder.addStatement(funContent, annotationValue)
        } else {
            //不是以 ) 结尾，说明未找到支持传递的类型
            messager.printMessage(Diagnostic.Kind.ERROR, "目前只支持传递 String、Int、Boolean")
        }
    }

    //获取值
    class Builder constructor(val parameter: ParameterSpec) {
        internal var messager: Messager? = null
        internal var className: ClassName? = null

        fun messager(messager: Messager): Builder {
            this.messager = messager
            return this
        }

        fun className(className: ClassName): Builder {
            this.className = className
            return this
        }

        fun build(): GParameterFactory {
            if (messager == null)
                throw IllegalArgumentException("messager为空，Messager用来报告错误、警告和其他提示信息")
            if (className == null)
                throw IllegalArgumentException("方法内容中的className为空")
            return GParameterFactory(this)
        }
    }
}