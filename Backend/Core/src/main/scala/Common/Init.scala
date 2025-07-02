package Common

import API.API
import DBAPI._

import cats.effect.IO
import io.circe.generic.auto._

object Init {
  def init: IO[Unit] = (for
    _ <- API.init
    _ <- SwitchDataSourceMessage("coursor").send
    _ <- StartTransactionMessage().send
  yield ()).handleErrorWith(err =>
    IO:
      println("[Error] Process.Init.init 失败, 请检查 db-manager 是否启动及端口问题")
      err.printStackTrace()
  )
}
