package APIs.CommentService

import Common.API.API
import Global.ServiceCenter.CommentService

case class DeleteCommentMessage(
    adminToken: String,
    commentId: String
) extends API[String](CommentService)
