package APIs.CommentService

import Common.API.API
import Global.ServiceCenter.CommentService
import Objects.Comment

case class UpdateCommentMessage(
    studentToken: String,
    comment: Comment
) extends API[String](CommentService)
