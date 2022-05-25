package data.mapper

import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.memberProperties

object MapperUtils {
    fun <T : Any> map(
        source: Any,
        destClass: KClass<T>,
        customFields: Map<String, Any> = emptyMap()
    ): T {
        val properties = source.javaClass.kotlin.memberProperties.associateBy { it.name }
        return destClass.constructors.first().callBy(
            destClass.constructors.first().parameters.associate { param ->
                val paramName = param.name
                val value = when (customFields[paramName] != null) {
                    true -> customFields[paramName]
                    false -> properties[paramName]?.get(source)
                        ?: handleMissingParam(param, source)
                }

                param to value
            }.filter { it.value != null }
        )
    }

    fun getMemberProperties(source: Any): Map<String, Any?> {
        return source.javaClass.kotlin.memberProperties.associate { it.name to it.get(source) }
    }

    private fun handleMissingParam(parameter: KParameter, source: Any): Any? {
        if (!parameter.isOptional) {
            throw IllegalArgumentException("`${parameter.name}` parameter is not found in $source")
        }
        return null
    }
}
