package Common

import API.API
import DBAPI.SwitchDataSourceMessage

import cats.effect.IO
import io.circe.generic.auto._

object Init {
  def init(initDB: IO[Unit]): IO[Unit] = (for
    _ <- API.init
    _ <- SwitchDataSourceMessage("Coursor").send
    _ <- initDB
  yield ()).handleErrorWith(err =>
    IO:
      println("[Error] Process.Init.init 失败, 请检查 db-manager 是否启动及端口问题")
      err.printStackTrace()
  )
}
