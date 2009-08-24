package net.scalaejb.service

/**
 * @author justin
 */
trait TestService {

  /**
   * Creates a message based on <code>arg</code>
   */
  def getMessage(arg: String) : String;
}