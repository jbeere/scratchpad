package net.scalaejb.service.impl

import javax.annotation.{Resource}
import javax.ejb.{Local, Remote, Stateless, SessionContext}
import service.{TestServiceLocal, TestServiceRemote}

/**
 * @author justin
 */
@Stateless
@Local {val value = Array(classOf[TestServiceLocal])}
@Remote {val value = Array(classOf[TestServiceRemote])}
class TestServiceBean extends TestServiceLocal with TestServiceRemote {
  
  val message = "Thank you, %s, for using this service! (Executed by %s)";

  @Resource
  var ctx: SessionContext = null

  def formatMessage(arg: String) = String.format(message, arg, ctx.getInvokedBusinessInterface)

  def getMessage(arg: String) = formatMessage(arg)

}