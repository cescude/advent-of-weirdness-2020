import scala.io.Source

object Main {

  case class Bag(adj: Set[String]) {
    override def toString: String = {
      adj.mkString(" ")
    }
  }

  object Bag {
    def parse(str: String): Bag = {
      // Turn something like "light red bags" into Bag(Set(light, red))
      Bag(str.split(" ").dropRight(1).toSet)
    }
  }

  case class Count(bag: Bag, amount: Long)

  object Count {
    def parse(str: String): Count = {
      // "4 light red bags" into Count(Bag(Set(light, red)), 4)
      val toks = str.split(" +")
      val count = toks.head.toLong
      Count(Bag.parse(toks.drop(1).mkString(" ")), count)
    }
  }

  case class Rule(bag: Bag, holds: Set[Count])

  object Rule {
    def parse(line: String): Rule = {
      // "light red bags contain 1 bright white bag, 2 muted yellow bags"
      // into Rule(Bag(Set(light,red)), Set(...))
      line.split(" +contain +") match {
        case Array(container, contains) =>
          val bag = Bag.parse(container)
          val holds = contains.split(" *, *").toSet
            .filter(_ != "no other bags.")
            .map(Count.parse)
          Rule(bag, holds)
      }
    }
  }

  val inp = Source.fromFile("../input").getLines.toSeq

  def main(args: Array[String]): Unit = {
    val rules = inp.map(Rule.parse)

    // rules.foreach(println)

    val shinyGoldBag = Bag(Set("shiny", "gold"))

    println("DFS Long", howManyL(shinyGoldBag, rules))
    println("DFS BigInt", howManyB(shinyGoldBag, rules))

    println("BFS Long", packBagL(shinyGoldBag, rules))
    println("BFS BigInt", packBagB(shinyGoldBag, rules))
  }

  // How many bags within `seed`?
  def howManyL(seed: Bag, rules: Seq[Rule]): Long = {
    val Some(Rule(_, holds)) = rules.find(_.bag == seed)

    val childCounts = holds.map({
      case Count(childBag, amount) =>
        amount + amount * howManyL(childBag, rules)
    })

    childCounts.sum
  }

  def howManyB(seed: Bag, rules: Seq[Rule]): BigInt = {
    val Some(Rule(_, holds)) = rules.find(_.bag == seed)

    val childCounts = holds.map({
      case Count(childBag, amount) =>
        amount + amount * howManyB(childBag, rules)
    })

    childCounts.sum
  }

  // Alternate `howMany` that manually counts the bags
  def packBagL(seed: Bag, rules: Seq[Rule]): Long = {
    def pack(count: Long, toPack: Seq[Bag]): Long = {
      if ( toPack.isEmpty ) return count

      val packingRecipe: Seq[Count] = toPack
        .flatMap(bag => rules.find(_.bag == bag).toSeq)
        .flatMap({ case Rule(_, holds) => holds })

      val nextToPack = packingRecipe.flatMap({
        case Count(bag, amount) => (1L to amount).map(_ => bag)
      })

      pack(count + toPack.size, nextToPack)
    }
    pack(0, Seq(seed)) - 1
  }

  def packBagB(seed: Bag, rules: Seq[Rule]): BigInt = {
    def pack(count: BigInt, toPack: Seq[Bag]): BigInt = {
      if ( toPack.isEmpty ) return count

      val packingRecipe: Seq[Count] = toPack
        .flatMap(bag => rules.find(_.bag == bag).toSeq)
        .flatMap({ case Rule(_, holds) => holds })

      val nextToPack = packingRecipe.flatMap({
        case Count(bag, amount) => (1L to amount).map(_ => bag)
      })

      pack(count + toPack.size, nextToPack)
    }
    pack(0, Seq(seed)) - 1
  }
}
