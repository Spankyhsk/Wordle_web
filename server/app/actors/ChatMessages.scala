package actors

import org.apache.pekko.actor.ActorRef

object ChatMessages {
  case class BroadcastMessage(content: String)

  case class Join(ref: ActorRef)

  case class Leave(ref: ActorRef)
}
