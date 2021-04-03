package shop.retries

import cats.syntax.show._
import org.typelevel.log4cats.Logger
import retry.RetryDetails._
import retry._

trait RetryHandler[F[_]] {
  def onError(retriable: Retriable)(e: Throwable, details: RetryDetails): F[Unit]
}

object RetryHandler {
  def apply[F[_]: RetryHandler]: RetryHandler[F] = implicitly

  implicit def forLogger[F[_]: Logger]: RetryHandler[F] =
    new RetryHandler[F] {
      def onError(retriable: Retriable)(e: Throwable, details: RetryDetails): F[Unit] =
        details match {
          case WillDelayAndRetry(_, retriesSoFar, _) =>
            Logger[F].error(
              s"Failed to process ${retriable.show} with ${e.getMessage}. So far we have retried $retriesSoFar times."
            )
          case GivingUp(totalRetries, _) =>
            Logger[F].error(s"Giving up on ${retriable.show} after $totalRetries retries.")
        }
    }
}
