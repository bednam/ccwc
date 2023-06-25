//> using file FileCounter.scala
//> using scala 2.13.11
//> using option -Wunused
//> using platform scala-native
//> using packaging.output ccwc
//> using dep com.monovore::decline-effect::2.4.1
//> using dep co.fs2::fs2-io::3.7.0

import cats.effect.std.Console
import cats.effect.{ExitCode, IO}
import com.monovore.decline.Opts
import com.monovore.decline.effect.CommandIOApp
import cats.syntax.all._
import java.nio.file.{Path => JavaPath}
import fs2.io.file.Path
import com.monovore.decline.Argument

object CCWC
    extends CommandIOApp(
      name = "ccwc",
      header = "coding challanges word count"
    ) {

  implicit val pathArgument: Argument[Path] =
    Argument[JavaPath].map(Path.fromNioPath)

  val bytes: Opts[IO[Long]] = Opts
    .flagOption[Path](long = "bytes", help = "byte count", short = "c")
    .map {
      case Some(path) => FileCounter[IO].bytes(path)
      case _          => FileCounter[IO].bytes()
    }

  val lines: Opts[IO[Long]] =
    Opts
      .flagOption[Path](long = "lines", help = "line count", short = "l")
      .map {
        case Some(path) => FileCounter[IO].lines(path)
        case _          => FileCounter[IO].lines()
      }

  val words: Opts[IO[Long]] =
    Opts
      .flagOption[Path](long = "words", help = "word count", short = "w")
      .map {
        case Some(path) => FileCounter[IO].words(path)
        case _          => FileCounter[IO].words()
      }

  val chars: Opts[IO[Long]] =
    Opts
      .flagOption[Path](long = "chars", help = "character count", short = "m")
      .map {
        case Some(path) => FileCounter[IO].chars(path)
        case _          => FileCounter[IO].chars()
      }

  val default: Opts[IO[(Long, Long, Long)]] =
    Opts.argument[Path]().orNone.map {
      case Some(path) =>
        (
          FileCounter[IO].lines(path),
          FileCounter[IO].words(path),
          FileCounter[IO].bytes(path)
        ).tupled
      case _ =>
        (
          FileCounter[IO].lines(),
          FileCounter[IO].words(),
          FileCounter[IO].bytes()
        ).tupled
    }

  override def main: Opts[IO[ExitCode]] =
    (bytes orElse lines orElse words orElse chars orElse default)
      .map(_.flatMap(Console[IO].println))
      .map(_.as(ExitCode.Success))
}
