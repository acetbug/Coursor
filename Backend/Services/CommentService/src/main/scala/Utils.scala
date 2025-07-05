import Common.DBAPI._
import Common.API.Planner
import Global._
import Objects._

import cats.effect.IO
import io.circe.Json
import io.circe.generic.auto._
import io.circe.syntax._

object Utils:
  val thisService = CommentService

  def init: IO[Unit] =
    for
      _ <- initSchema(thisService.schema)
      _ <- writeDB(
        s"""
         |CREATE TABLE IF NOT EXISTS ${thisService.schema}.${thisService.commentTable} (
         |    id SERIAL PRIMARY KEY,
         |    teacher_id VARCHAR NOT NULL
         |        REFERENCES ${UserService.schema}.${UserService.userTable}(id)
         |        ON DELETE CASCADE,
         |    subject_id INT NOT NULL
         |        REFERENCES ${SubjectService.schema}.${SubjectService.subjectTable}(id)
         |        ON DELETE CASCADE,
         |    content TEXT NOT NULL
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
      case "CreateCommentMessage" =>
        Planner.execute[CreateCommentMessagePlanner, String](requestJson)
      case "DeleteCommentMessage" =>
        Planner.execute[DeleteCommentMessagePlanner, Unit](requestJson)
      case "QueryCommentsMessage" =>
        Planner.execute[QueryCommentsMessagePlanner, List[Comment]](requestJson)
      case _ =>
        IO.raiseError(
          new Exception(s"Unknown message type: $messageName")
        )

  def createComment(
      comment: Comment
  ): IO[String] =
    val sql =
      s"""
       |INSERT INTO ${thisService.schema}.${thisService.commentTable} (teacher_id, subject_id, content)
       |VALUES (?, ?, ?);
      """.stripMargin

    val params = List(
      SqlParameter("String", comment.teacherId),
      SqlParameter("Int", comment.subjectId),
      SqlParameter("String", comment.content)
    )

    for id <- readDBInt(sql, params) yield id.toString

  def deleteComment(
      commentId: String
  ): IO[Unit] =
    val sql =
      s"""
       |DELETE FROM ${thisService.schema}.${thisService.commentTable}
       |WHERE id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("Int", commentId)
      )

    for _ <- writeDB(sql, params) yield ()

  def queryComments(
      teacherId: String,
      subjectId: String
  ): IO[List[Comment]] =
    val sql =
      s"""
       |SELECT *
       |FROM ${thisService.schema}.${thisService.commentTable}
       |WHERE teacher_id = ? AND subject_id = ?;
      """.stripMargin

    val params =
      List(
        SqlParameter("String", teacherId),
        SqlParameter("Int", subjectId)
      )

    for
      rows <- readDBRows(sql, params)

      comments =
        rows.map: row =>
          Comment(
            id = decodeField[Int](row, "id").toString,
            teacherId = decodeField[String](row, "teacher_id"),
            subjectId = decodeField[Int](row, "subject_id").toString,
            content = decodeField[String](row, "content")
          )
    yield comments
