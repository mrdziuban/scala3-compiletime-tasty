# Scala 3 `compiletime` TASTy

This repository demonstrates a nuance of Scala 3's TASTy output when using various `inline`/`compiletime` methods to
summon `given`/`implicit` instances.

When calling `summon` and `scala.compiletime.summonInline`, the TASTy file contains a reference to the `given`/`implicit`
instance that satisfied the implicit search.

When calling `scala.compiletime.summonAll` and `scala.compiletime.summonFrom`, however, the TASTy does not include this
reference.

[`Test.scala`](src/main/scala/example/Test.scala) summons an instance of `cats.Show[String]` using these 4 methods:

```scala
val instWithSummon = summon[cats.Show[String]]
val instWithSummonAll = compiletime.summonAll[cats.Show[String] *: EmptyTuple]
val instWithSummonInline = compiletime.summonInline[cats.Show[String]]
inline def instWithSummonFrom = compiletime.summonFrom { case s: cats.Show[String] => s }
```

[`TastyQueryTest.scala`](src/test/scala/example/TastyQueryTest.scala) then uses
[tasty-query](https://github.com/scalacenter/tasty-query) to log the AST of each of the method calls. Its output is below.

<details>

<summary>expand</summary>

```scala
********************************************************************************
instWithSummon:
  Inlined(
    expr = Inlined(
      expr = Ident(
        name = UniqueName(underlying = SimpleName(name = "x"), separator = "$proxy", num = 1)
      ),
      caller = None,
      bindings = List()
    ),
    caller = Some(
      value = TypeIdent(name = ObjectClassTypeName(underlying = SimpleTypeName(name = "Predef")))
    ),
    bindings = List(
      ValDef(
        name = UniqueName(underlying = SimpleName(name = "x"), separator = "$proxy", num = 1),
        tpt = TypeWrapper(
          tp = AppliedType(TypeRef(PackageRef(cats), Show), List(TypeRef(TermRef(PackageRef(scala), Predef), String)))
        ),
        rhs = Some(value = Ident(name = SimpleName(name = "catsShowForString"))),
        symbol = symbol[instWithSummon>x$proxy1]
      )
    )
  )
********************************************************************************

********************************************************************************
instWithSummonAll:
  TypeApply(
    fun = Select(
      qualifier = Select(
        qualifier = Ident(name = SimpleName(name = "compiletime")),
        name = SimpleName(name = "package$package")
      ),
      name = SignedName(
        underlying = SimpleName(name = "summonAll"),
        sig = Signature(
          paramsSig = List(TypeLen(len = 1)),
          resSig = SignatureName(
            items = List(SimpleName(name = "scala"), SimpleName(name = "Product"))
          )
        ),
        target = SimpleName(name = "summonAll")
      )
    ),
    args = List(
      AppliedTypeTree(
        tycon = TypeIdent(name = SimpleTypeName(name = "*:")),
        args = List(
          AppliedTypeTree(
            tycon = SelectTypeTree(
              qualifier = TypeWrapper(tp = PackageRef(cats)),
              name = SimpleTypeName(name = "Show")
            ),
            args = List(TypeIdent(name = SimpleTypeName(name = "String")))
          ),
          TypeIdent(name = SimpleTypeName(name = "EmptyTuple"))
        )
      )
    )
  )
********************************************************************************

********************************************************************************
instWithSummonInline:
  Ident(name = SimpleName(name = "catsShowForString"))
********************************************************************************

********************************************************************************
instWithSummonFrom:
  Typed(
    expr = InlineMatch(
      selector = None,
      cases = List(
        CaseDef(
          pattern = Bind(
            name = SimpleName(name = "s"),
            body = TypeTest(
              body = WildcardPattern(
                tpe = AppliedType(TypeRef(PackageRef(cats), Show), List(TypeRef(TermRef(PackageRef(scala), Predef), String)))
              ),
              tpt = AppliedTypeTree(
                tycon = SelectTypeTree(
                  qualifier = TypeWrapper(tp = PackageRef(cats)),
                  name = SimpleTypeName(name = "Show")
                ),
                args = List(TypeIdent(name = SimpleTypeName(name = "String")))
              )
            ),
            symbol = symbol[instWithSummonFrom>s]
          ),
          guard = None,
          body = Block(
            stats = List(),
            expr = Typed(
              expr = Ident(name = SimpleName(name = "s")),
              tpt = TypeWrapper(
                tp = AppliedType(TypeRef(PackageRef(cats), Show), List(TypeRef(TermRef(PackageRef(scala), Predef), String)))
              )
            )
          )
        )
      )
    ),
    tpt = TypeWrapper(
      tp = AppliedType(TypeRef(PackageRef(cats), Show), List(TypeRef(TermRef(PackageRef(scala), Predef), String)))
    )
  )
********************************************************************************
```

</details>

Note that both `instWithSummon` and `instWithSummonInline` contain a reference to `catsShowForString`, which is
[the instance](https://github.com/typelevel/cats/blob/v2.13.0/core/src/main/scala/cats/Show.scala#L98) that satisfies the implicit search, but neither `instWithSummonAll` nor `instWithSummonFrom` have a reference to it.
