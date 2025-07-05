import APIs.AuthService.CheckAuthMessage
import Common.API.Planner
import Objects._

import cats.effect.IO
import io.circe.generic.auto._

case class CreateCommentMessagePlanner(
    studentToken: String,
    comment: Comment
) extends Planner[String]:
  def plan: IO[String] =
    for
      _ <- CheckAuthMessage(studentToken, UserRole.Student).send
      commentId <- Utils.createComment(comment)
    yield commentId

case class DeleteCommentMessagePlanner(
    adminToken: String,
    commentId: String
) extends Planner[Unit]:
  def plan: IO[Unit] =
    for
      _ <- CheckAuthMessage(adminToken, UserRole.Admin).send
      _ <- Utils.deleteComment(commentId)
    yield ()

case class QueryCommentsMessagePlanner(
    teacherId: String,
    subjectId: String
) extends Planner[List[Comment]]:
  def plan: IO[List[Comment]] =
    Utils.queryComments(teacherId, subjectId)
