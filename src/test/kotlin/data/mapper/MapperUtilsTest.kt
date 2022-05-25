package data.mapper

import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

internal class MapperUtilsTest {
    @Test
    fun `simple map`() {
        val source = SourceTestClass(
            id = 1, name = "name", intList = listOf(1, 2, 3), customType = CustomType(customValue = "customValue")
        )

        val destClass: DestTestClass = MapperUtils.map(source, DestTestClass::class)

        val expected = DestTestClass(
            id = 1, name = "name", intList = listOf(1, 2, 3), customType = CustomType(customValue = "customValue")
        )
        assertEquals(expected, destClass)
    }

    @Test
    fun `map with overridden field`() {
        val source = SourceTestClass(
            id = 1, name = "name", intList = listOf(1, 2, 3), customType = CustomType(customValue = "customValue")
        )

        val destClass: DestTestClass =
            MapperUtils.map(
                source, DestTestClass::class,
                mapOf(
                    DestTestClass::name.name to "overridden name"
                )
            )

        val expected = DestTestClass(
            id = 1,
            name = "overridden name",
            intList = listOf(1, 2, 3),
            customType = CustomType(customValue = "customValue")
        )
        assertEquals(expected, destClass)
    }

    @Test
    fun `map with overridden fields`() {
        val source = SourceTestClass(
            id = 1, name = "name", intList = listOf(1, 2, 3), customType = CustomType(customValue = "customValue")
        )

        val destClass: DestTestClass =
            MapperUtils.map(
                source, DestTestClass::class,
                mapOf(
                    DestTestClass::name.name to "overridden name",
                    DestTestClass::customField.name to "my custom field",
                    DestTestClass::customType.name to CustomType(customValue = "overridden")
                )
            )

        val expected = DestTestClass(
            id = 1,
            name = "overridden name",
            intList = listOf(1, 2, 3),
            customType = CustomType(customValue = "overridden"),
            customField = "my custom field"
        )
        assertEquals(expected, destClass)
    }
}

data class SourceTestClass(
    override val id: Int? = null,
    override val name: String,
    override val intList: List<Int>,
    override val customType: CustomType,
) : ITestClass

data class DestTestClass(
    override val id: Int? = null,
    override val name: String,
    override val intList: List<Int>,
    override val customType: CustomType,
    val customField: String = "defaultCustomValue",
) : ITestClass

class CustomType(val customValue: String) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CustomType

        if (customValue != other.customValue) return false

        return true
    }

    override fun hashCode(): Int {
        return customValue.hashCode()
    }
}

interface ITestClass {
    val id: Int?
    val name: String
    val intList: List<Int>
    val customType: CustomType
}
