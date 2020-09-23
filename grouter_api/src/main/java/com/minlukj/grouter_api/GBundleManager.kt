package com.minlukj.groute_api

import android.content.Context
import android.os.Bundle
import android.os.Parcelable
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityOptionsCompat
import com.minlukj.grouter_api.GRouterManager
import java.io.Serializable

/**
 *
 * @ProjectName:    Module_Study
 * @Package:        com.minlukj.groute_api
 * @ClassName:      GBundleManager
 * @Description:    跳转时 Intent 传递参数
 * @Author:         忞鹿
 * @CreateDate:     2020/9/17 15:37
 */
class GBundleManager {
    var bundle = Bundle()

    var optionsCompat = null
    var enterAnim = -1//入场动画
    var exitAnim = -1//出场动画

    /**
     * Inserts a String value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a String, or null
     */
    fun withString(key: String, value: String): GBundleManager {
        bundle.putString(key, value)
        return this
    }

    /**
     * Inserts a Boolean value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a boolean
     */
    fun withBoolean(key: String, value: Boolean): GBundleManager {
        bundle.putBoolean(key, value)
        return this
    }

    /**
     * Inserts a byte value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a byte
     */
    fun withByte(key: String, value: Byte): GBundleManager {
        bundle.putByte(key, value)
        return this
    }

    /**
     * Inserts a short value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a short
     */
    fun withShort(key: String, value: Short): GBundleManager {
        bundle.putShort(key, value)
        return this
    }

    /**
     * Inserts an int value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value an int
     */
    fun withInt(key: String, value: Int): GBundleManager {
        bundle.putInt(key, value)
        return this
    }

    /**
     * Inserts a long value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a long
     */
    fun withLong(key: String, value: Long): GBundleManager {
        bundle.putLong(key, value)
        return this
    }

    /**
     * Inserts a float value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a float
     */
    fun withFloat(key: String, value: Float): GBundleManager {
        bundle.putFloat(key, value)
        return this
    }

    /**
     * Inserts a double value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a double
     */
    fun withDouble(key: String, value: Double): GBundleManager {
        bundle.putDouble(key, value)
        return this
    }

    /**
     * Inserts a char value into the mapping of this Bundle, replacing
     * any existing value for the given key.
     *
     * @param key a String, or null
     * @param value a char
     */
    fun withChar(key: String, value: Char): GBundleManager {
        bundle.putChar(key, value)
        return this
    }

    /**
     * Inserts a Serializable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a Serializable object, or null
     */
    fun withSerializable(key: String, value: Serializable): GBundleManager {
        bundle.putSerializable(key, value)
        return this
    }

    /**
     * Inserts a Parcelable value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a Parcelable object, or null
     */
    fun withParcelable(key: String, value: Parcelable): GBundleManager {
        bundle.putParcelable(key, value)
        return this
    }

    /**
     * Inserts a byte array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a byte array object, or null
     */
    fun withByteArray(key: String, vararg value: Byte): GBundleManager {
        bundle.putByteArray(key, value)
        return this
    }

    /**
     * Inserts a short array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a short array object, or null
     */
    fun withShortArray(key: String, vararg value: Short): GBundleManager {
        bundle.putShortArray(key, value)
        return this
    }

    /**
     * Inserts an int array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value an int array object, or null
     */
    fun withIntArray(key: String, vararg value: Int): GBundleManager {
        bundle.putIntArray(key, value)
        return this
    }

    /**
     * Inserts a long array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a long array object, or null
     */
    fun withLongArray(key: String, vararg value: Long): GBundleManager {
        bundle.putLongArray(key, value)
        return this
    }

    /**
     * Inserts a float array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a float array object, or null
     */
    fun withFloatArray(key: String, vararg value: Float): GBundleManager {
        bundle.putFloatArray(key, value)
        return this
    }

    /**
     * Inserts a double array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a double array object, or null
     */
    fun withDoubleArray(key: String, vararg value: Double): GBundleManager {
        bundle.putDoubleArray(key, value)
        return this
    }

    /**
     * Inserts a char array value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a char array object, or null
     */
    fun withCharArra(key: String, vararg value: Char): GBundleManager {
        bundle.putCharArray(key, value)
        return this
    }

    fun withBundle(bundle: Bundle): GBundleManager {
        this.bundle = bundle
        return this
    }

    /**
     * Inserts a Bundle value into the mapping of this Bundle, replacing
     * any existing value for the given key.  Either key or value may be null.
     *
     * @param key a String, or null
     * @param value a Bundle object, or null
     */
    fun withBundle(key: String, bundle: Bundle): GBundleManager {
        this.bundle.putBundle(key, bundle)
        return this
    }

    /**
     * Set normal transition anim
     *
     * @param enterAnim enter
     * @param exitAnim  exit
     */
    fun withTransition(enterAnim: Int, exitAnim: Int): GBundleManager {
        this.enterAnim = enterAnim
        this.exitAnim = exitAnim
        return this
    }

    /**
     * Set options compat
     *
     * @param compat compat
     */
    @RequiresApi(16)
    fun withOptionsCompat(compat: ActivityOptionsCompat): GBundleManager {
        optionsCompat ?: compat.toBundle()
        return this
    }

    /**
     * Start activity
     *
     * @param context context
     * @param request activity request
     */
    fun navigation(context: Context, request: Int = -1): Any? {
        return GRouterManager.instance.navigation(context, this, request)
    }
}