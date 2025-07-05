import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = TermService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.termTable} (
         |    id SERIAL PRIMARY KEY,
         |    name VARCHAR NOT NULL,
         |    phase VARCHAR NOT NULL
         |);
        """.stripMargin,
        List()
      )
    yield ()

  def handleRequest(
      messageName: String,
      requestJson: Json
  ): IO[Json] =
    messageName match
      case "DeleteTermMessage" =>
        Planner.execute[DeleteTermMessagePlanner, Unit](requestJson)
      case "QueryTermsMessage" =>
        Planner.execute[QueryTermsMessagePlanner, List[Term]](requestJson)
      case "UpdateTermMessage" =>
        Planner.execute[UpdateTermMessagePlanner, String](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def deleteTerm(
      termId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.termTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", termId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryTerms(
      termIds: List[String]
  ): IO[List[Term]] =
    val isAll = termIds.isEmpty

    val sql =
      if isAll then s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.termTable};
        """.stripMargin
      else s"""
         |SELECT *
         |FROM ${thisService.schema}.${thisService.termTable}
         |WHERE id IN (${termIds.map(_ => "?").mkString(", ")});
        """.stripMargin

    val params: List[SqlParameter] =
      if isAll then Nil
      else termIds.map(id => SqlParameter("Int", id))

    for
      rows <- readDBRows(sql, params)

      terms =
        rows.map: row =>
          Term(
            id = decodeField[Int](row, "id").toString,
            name = decodeField[String](row, "name"),
            phase = decodeField[TermPhase](row, "phase")
          )
    yield terms

  def updateTerm(
      term: Term
  ): IO[String] =
    val insertSql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.termTable} (name, phase)
       |VALUES (?, ?)
       |RETURNING id;
      """.stripMargin

    val insertParams =
      List(
        SqlParameter("String", term.name),
        SqlParameter("String", term.phase.toString)
      )

    val updateSql =
      s"""
       |UPDATE ${thisService.schema}.${thisService.termTable}
       |SET name = ?, phase = ?
       |WHERE id = ?;
      """.stripMargin

    val updateParams =
      List(
        SqlParameter("String", term.name),
        SqlParameter("String", term.phase.toString),
        SqlParameter("Int", term.id)
      )

    if term.id.isEmpty then
      for id <- readDBInt(insertSql, insertParams)
      yield id.toString
    else
      for _ <- writeDB(updateSql, updateParams)
      yield term.id

  def switchTermPhase(
      termId: String
  ): IO[Unit] =
    val sql =
      s"""
       |WITH updated AS (
       |    UPDATE ${thisService.schema}.${thisService.termTable}
       |    SET phase = ?
       |    WHERE id = ? AND phase = ?
       |    RETURNING 1
       |);
       |SELECT EXISTS(SELECT 1 FROM updated);
      """.stripMargin

    val params =
      List(
        SqlParameter("String", TermPhase.Confirmed.toString),
        SqlParameter("Int", termId),
        SqlParameter("String", TermPhase.InSelection.toString)
      )

    for
      success <- readDBBoolean(sql, params)
      _ <-
        if success then IO.unit
        else IO.raiseError(new Exception("Term has already been confirmed"))
    yield ()
