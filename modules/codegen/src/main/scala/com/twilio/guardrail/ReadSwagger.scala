package com.twilio.guardrail

import _root_.io.swagger.models.Swagger
import _root_.io.swagger.parser._
import cats._
import java.nio.file.Path
import scala.io.AnsiColor
import scala.util.Try

case class ReadSwagger[T](path: Path, next: Swagger => T)
object ReadSwagger {
  def unsafeReadSwagger[T: Monoid](rs: ReadSwagger[T]): T =
    (for {
      absolutePath <- Try(rs.path.toAbsolutePath.toString).toOption
      swagger      <- Option(new SwaggerParser().read(absolutePath))
    } yield rs.next(swagger))
      .getOrElse {
        println(s"${AnsiColor.RED}Spec file ${rs.path} is either incorrectly formatted or missing.${AnsiColor.RESET}")
        Monoid.empty[T]
      }
}
