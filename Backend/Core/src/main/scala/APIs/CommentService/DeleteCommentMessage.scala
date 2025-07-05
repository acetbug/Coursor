package APIs.CommentService

import Common.API.API
import Global.CommentService

case class DeleteCommentMessage(
    adminToken: String,
    commentId: String
) extends API[Unit](CommentService, "DeleteCommentMessage")
