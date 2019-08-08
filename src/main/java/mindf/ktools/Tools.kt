package mindf.ktools

import com.google.gson.Gson
import com.google.gson.JsonElement
import org.json.JSONObject
import java.io.IOException
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty1
import kotlin.reflect.KProperty1
import kotlin.reflect.full.memberProperties
import kotlin.reflect.jvm.javaField
import kotlin.streams.asSequence

class Tools {

    companion object {

        fun xor(condition: Boolean): Boolean {
            return !condition
        }

        fun jsonToModel(jsonObjects: MutableList<JSONObject>, model: Class<out Any>): MutableList<*> {
            val builtModel: MutableList<Any> = arrayListOf()
            val gson = Gson()
            for (jsonObject in jsonObjects) {
                val jsonElement: JsonElement = gson.fromJson(jsonObject.toString(), JsonElement::class.java)
                val employee = gson.fromJson(jsonElement, model.newInstance().javaClass)
                builtModel.add(employee)
            }
            return builtModel
        }

        fun getDatabaseProperties(name: String): Properties {
            val classLoader = Thread.currentThread().contextClassLoader
            val properties = Properties()
            try {
                classLoader.getResourceAsStream(name)!!.use { resourceStream ->
                    properties.load(
                            resourceStream
                    )
                }
            } catch (e: IOException) {
                e.printStackTrace()
                throw IllegalArgumentException("database.properties file not found!")
            }
            return properties
        }

        @Suppress("UNCHECKED_CAST")
        fun readInstanceProperty(instance: Any, propertyName: String): Any? {
            val property = instance::class.memberProperties.first {
                it.name == propertyName
            } as KProperty1<Any, *>
            return property.get(instance)
        }

        fun containsPropertyValue(
            list: MutableList<*>,
            kMutableProperty1: KMutableProperty1<*, String>,
            value: String
        ): Boolean {
            list.forEach { element ->
                if (readInstanceProperty(element!!, kMutableProperty1.name) == value) {
                    return true
                }
            }
            return false
        }

        fun generateSerialNumber(
            charSet: String = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ123456789",
            length: Long = 20,
            invalidCharSet: String = "4Fu",
            randomNumberOrigin: Int = 0
        ): String {
            charSet.removePrefix(invalidCharSet)
            return java.util.Random().ints(
                length, randomNumberOrigin, charSet.length
            ).asSequence().map(
                charSet::get
            ).joinToString("")
        }

        inline fun <reified T> findAnnotation(kProperty1: KMutableProperty1<*, *>): T? {
            val annotations = kProperty1.javaField!!.annotations
            for (annotation in annotations) {
                if (annotation::class == T::class) {
                    return annotation as T
                }
            }
            return null
        }

        fun findAnnotation(kProperty1: KMutableProperty1<*, *>, kClass: KClass<*>): Annotation? { //todo fix
            val annotations = kProperty1.javaField!!.annotations
            for (annotation in annotations) {
                if (annotation::class == kClass::class) {
                    return annotation
                }
            }
            return null
        }
    }
}