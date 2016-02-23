package todomvc.server

import com.twitter.util.{Future, Promise}
import scalaz.\/
import scalaz.concurrent.Task

object AsyncImplicits extends AsyncImplicits

trait AsyncImplicits {
  implicit class TaskOps[A](task: Task[A]) {
    def toFuture: Future[A] = {
      val promise = Promise[A]
      task.runAsyncInterruptibly { dis: Throwable \/ A =>
        dis.fold(
          exn => promise.setException(exn),
          ans => promise.setValue(ans)
        )
      }
      promise
    }
  }
}
