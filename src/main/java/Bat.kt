import java.io.File

class Bat {

    companion object {

        fun createFileCommand(fileName: String): String {
            return "cmd /c echo 2>$fileName"
        }

        fun createFile(fileName: String): Process {
            return execute(createFileCommand(fileName))!!
        }

        fun createFile(name: String, content: String = ""): File {
            val file = File(name)
            file.writeText(content)
            return file
        }

        fun execute(command: String): Process? {
            return Runtime.getRuntime().exec(command)
        }
    }
}