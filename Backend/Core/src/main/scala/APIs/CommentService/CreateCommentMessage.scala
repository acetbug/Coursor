package APIs.CommentService

import Common.API.API
import Global.CommentService
import Objects.Comment

case class CreateCommentMessage(
    studentToken: String,
    comment: Comment
) extends API[String](CommentService, "CreateCommentMessage")
