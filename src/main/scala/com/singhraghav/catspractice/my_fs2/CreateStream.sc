import fs2._

val s: Stream[Pure, Int] = Stream(1, 2, 3, 4)
val s2: Stream[Pure, Int] = Stream.empty
val s3 = Stream.emit(42)
val s4 = Stream.emits(List(2, 3, 4))

val s5 = Stream.iterate(1)(_ + 1)

val s6 = Stream.unfold(1)(s => if(s > 15) None else Some((s, s * 2)))

val s7 = Stream.range(1, 15)

//s.toList
//s2.toList
//s3.toList
//s4.toList

//s5.take(10).toList


//s6.toList

//s7.toList

def letterIter: Stream[Pure, Char] = Stream.unfold(1)(value => if(value >= 27) None else Some(((value + 64).toChar, value + 1)))

def letterIter2: Stream[Pure, Char] = Stream.iterate('a')(c => (c + 1).toChar).take(26)

letterIter.toList

letterIter2.toList

val nats = Stream.iterate(1)(_ + 1)

val oddNumStream = nats.map(n => 2* n - 1)

oddNumStream.take(10).toList























