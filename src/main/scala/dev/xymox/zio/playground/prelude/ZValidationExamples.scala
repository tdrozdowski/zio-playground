package dev.xymox.zio.playground.prelude

import zio._
import zio.console._
import zio.prelude.ZValidation

import scala.util.matching.Regex

object ZValidationExamples extends App {

  object BasicValidations {

    val VIN_REGEX = "[A-HJ-NPR-Z0-9]{17}".r

    def nonEmptyString(fieldName: String, value: String): ZValidation[String, FieldError, String] =
      if (value.nonEmpty)
        ZValidation.succeed(value).log(s"$fieldName with value $value is OK!")
      else
        ZValidation.fail(FieldError(fieldName, "was empty!")).log("empty string!")

    def validVIN(fieldName: String, value: String): ZValidation[String, FieldError, String] =
      value match {
        case VIN_REGEX(_*) => ZValidation.succeed(value).log(s"$fieldName with value $value is OK!")
        case _             => ZValidation.fail(FieldError(fieldName, "contains an invalid VIN.")).log(s"invalid vin: $value")
      }
  }

  case class FieldError(field: String, message: String) extends Throwable

  def validationExample = {
    val emptyValidation      = BasicValidations.nonEmptyString("email", "")
    val vinValidation        = BasicValidations.validVIN("vin", "WBAWL73589P473132")
    val invalidVinValidation = BasicValidations.validVIN("vin2", "asdfasdf")
    for {
      overallResults <- ZIO(ZValidation.validate(emptyValidation, vinValidation, invalidVinValidation))
      _              <- putStrLn(s"Validation results: $overallResults")
    } yield ()
  }

  override def run(args: List[String]): URIO[zio.ZEnv, ExitCode] = validationExample.exitCode
}
