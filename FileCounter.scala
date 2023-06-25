import cats.effect.Async
import cats.syntax.functor._

import fs2.text
import fs2.io._
import fs2.io.file.{Files, Path}

trait FileCounter[F[_]] {
  def bytes(filePath: Path): F[Long]
  def bytes(): F[Long]
  def lines(filePath: Path): F[Long]
  def lines(): F[Long]
  def words(filePath: Path): F[Long]
  def words(): F[Long]
  def chars(filePath: Path): F[Long]
  def chars(): F[Long]
}

object FileCounter {
  def apply[F[_]: FileCounter]: FileCounter[F] = implicitly

  implicit def of[F[_]: Async: Files]: FileCounter[F] =
    new FileCounter[F] {
      override def bytes(filePath: Path): F[Long] =
        Files[F].readAll(filePath).compile.count

      override def bytes(): F[Long] =
        stdin[F](1024).compile.count

      override def lines(filePath: Path): F[Long] =
        Files[F]
          .readUtf8Lines(filePath)
          .compile
          .count
          .map(_ - 1)

      override def lines(): F[Long] =
        stdinUtf8[F](1024)
          .through(text.lines)
          .compile
          .count
          .map(_ - 1)

      override def words(filePath: Path): F[Long] =
        Files[F]
          .readUtf8Lines(filePath)
          .map(_.split("\\s+").filterNot(_.isBlank).length)
          .compile
          .foldMonoid
          .map(_.toLong)

      override def words(): F[Long] =
        stdinUtf8[F](1024)
          .through(text.lines)
          .map(_.split("\\s+").filterNot(_.isBlank).length)
          .compile
          .foldMonoid
          .map(_.toLong)

      override def chars(filePath: Path): F[Long] =
        Files[F]
          .readUtf8(filePath)
          .map(_.length)
          .compile
          .foldMonoid
          .map(_.toLong)
          .map(_ + 1)

      override def chars(): F[Long] =
        stdinUtf8[F](1024)
          .map(_.length)
          .compile
          .foldMonoid
          .map(_.toLong)
          .map(_ + 1)
    }
}
