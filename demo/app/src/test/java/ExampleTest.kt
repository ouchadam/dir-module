import CommonModelFixture.aCommonModel
import org.junit.*

class ExampleTest {

    @Test
    fun `using exported module`() {
        val importedModuleModel = aCommonModel()

        assert(importedModuleModel.a == "a")
    }
}