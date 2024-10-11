import mill._
import $ivy.`com.lihaoyi::mill-contrib-playlib:`,  mill.playlib._

object server extends PlayModule with SingleModule {

  def scalaVersion = "3.3.1"
  def playVersion = "3.0.0"
  def twirlVersion = "2.0.1"

  object test extends PlayTests
}
