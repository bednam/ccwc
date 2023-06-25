//> using scala 2.13.11
//> using test.dep org.scalameta::munit::0.7.29
//> using test.dep com.lihaoyi::os-lib::0.9.1

class CCWCTest extends munit.FunSuite {
  private val filePath: String = "pg132.txt"

  private def assertFile(flag: String): Unit = {
    val wcOutput = os.proc("wc", flag, filePath).call()
    val wcBytes = wcOutput.out.text.trim.split(" ").head
    val ccwcOutput = os
      .proc(
        "./ccwc",
        s"$flag$filePath"
      )
      .call()
    val ccwcBytes = ccwcOutput.out.text.trim

    assertEquals(ccwcBytes, wcBytes)
  }

  private def assertStdIn(flag: String): Unit = {
    val wcOutput = os.proc("wc", flag, filePath).call()
    val wcBytes = wcOutput.out.text.trim.split(" ").head
    val cat = os.proc("cat", "pg132.txt").spawn()
    val ccwcOutput = os.proc("./ccwc", flag).call(stdin = cat.stdout)
    val ccwcBytes = ccwcOutput.out.text.trim

    assertEquals(ccwcBytes, wcBytes)
  }

  test("count bytes in file") {
    assertFile("-c")
  }

  test("count bytes in stdin") {
    assertStdIn("-c")
  }

  test("count lines in file") {
    assertFile("-l")
  }

  test("count lines in stdin") {
    assertStdIn("-l")
  }

  test("count words in file") {
    assertFile("-w")
  }

  test("count words in stdin") {
    assertStdIn("-w")
  }

  test("count characters in file") {
    assertFile("-m")
  }

  test("count characters in stdin") {
    assertStdIn("-m")
  }
}
