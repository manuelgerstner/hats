package models

import models._

case class SessionConfig(
  topic: String,
  whiteTimeLimit: Option[Int],
  whiteAloneTime: Option[Int],
  yellowTimeLimit: Option[Int],
  yellowAloneTime: Option[Int],
  redTimeLimit: Option[Int],
  redAloneTime: Option[Int],
  greenTimeLimit: Option[Int],
  greenAloneTime: Option[Int],
  blueTimeLimit: Option[Int],
  blueAloneTime: Option[Int],
  blackTimeLimit: Option[Int],
  blackAloneTime: Option[Int],
  mailAddresses: String) {

  def get(num: Option[Int]) = num match {
    case Some(x) => x
    case None    => -1
  }

  def white(sessionId: Long): HatFlow = HatFlow(0, sessionId, 1, get(whiteTimeLimit), get(whiteAloneTime));
  def red(sessionId: Long): HatFlow = HatFlow(1, sessionId, 2, get(redTimeLimit), get(redAloneTime));
  def yellow(sessionId: Long): HatFlow = HatFlow(2, sessionId, 3, get(yellowTimeLimit), get(yellowAloneTime));
  def black(sessionId: Long): HatFlow = HatFlow(3, sessionId, 4, get(blackTimeLimit), get(blackAloneTime));
  def green(sessionId: Long): HatFlow = HatFlow(4, sessionId, 5, get(greenTimeLimit), get(greenAloneTime));
  def blue(sessionId: Long): HatFlow = HatFlow(5, sessionId, 6, get(blueTimeLimit), get(blueAloneTime));
  def mailAddressList: List[String] = mailAddresses.split(",").toList;

}