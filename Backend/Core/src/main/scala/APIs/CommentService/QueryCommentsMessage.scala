package APIs.CommentService

import Common.API.API
import Global.CommentService
import Objects.Comment

case class QueryCommentsMessage(
    teacherId: String,
    subjectId: String
) extends API[List[Comment]](CommentService, "QueryCommentsMessage")
