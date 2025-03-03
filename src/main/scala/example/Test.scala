package example

object Test {
  val instWithSummon = summon[cats.Show[String]]
  val instWithSummonAll = compiletime.summonAll[cats.Show[String] *: EmptyTuple]
  val instWithSummonInline = compiletime.summonInline[cats.Show[String]]
  inline def instWithSummonFrom = compiletime.summonFrom { case s: cats.Show[String] => s }
}
