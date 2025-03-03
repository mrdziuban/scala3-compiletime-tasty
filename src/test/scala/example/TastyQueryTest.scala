package example

import java.net.URI
import java.nio.file.FileSystems
import tastyquery.Contexts.Context
import tastyquery.jdk.ClasspathLoaders
import tastyquery.Trees.{DefDef, Tree, ValDef}

object TastyQueryTest {
  val paths = FileSystems.getFileSystem(URI.create("jrt:/")).getPath("modules", "java.base") :: Classpath.paths.toList

  given ctx: Context = Context.initialize(ClasspathLoaders.read(paths))

  def getDefDef(name: String): Tree = ctx.findStaticTerm(name).tree.get.asInstanceOf[DefDef].rhs.get
  def getValDef(name: String): Tree = ctx.findStaticTerm(name).tree.get.asInstanceOf[ValDef].rhs.get

  val instWithSummon = getValDef("example.Test.instWithSummon")
  val instWithSummonAll = getValDef("example.Test.instWithSummonAll")
  val instWithSummonInline = getValDef("example.Test.instWithSummonInline")
  val instWithSummonFrom = getDefDef("example.Test.instWithSummonFrom")

  val stars = "*" * 80

  def main(args: Array[String]): Unit =
    println(List(
      "instWithSummon" -> instWithSummon,
      "instWithSummonAll" -> instWithSummonAll,
      "instWithSummonInline" -> instWithSummonInline,
      "instWithSummonFrom" -> instWithSummonFrom,
    ).map((name, valDef) =>
      s"$stars\n$name:\n  ${pprint(valDef).toString.replace("\n", "\n  ")}\n$stars"
    ).mkString("\n\n"))
}
