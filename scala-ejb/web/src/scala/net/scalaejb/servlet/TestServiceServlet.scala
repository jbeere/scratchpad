package net.scalaejb.servlet

import java.io.PrintWriter
import javax.naming.{Context, InitialContext}
import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}
import scalaejb.service.TestService
import scalaejb.util.Lookup
import util.Lookup

/**
 * @author justin
 */
class TestServiceServlet extends HttpServlet {

  // the usual HttpServlet.doGet method, just syntactically different
  override def doGet(request: HttpServletRequest, response: HttpServletResponse) = {
    // the usual response writer
    val writer: PrintWriter = response.getWriter
    // print the result of the service method to the response writer
    print(writer, service.getMessage(parameter(request, "arg")))
    // do the same thing but using the remote instance
    // print(writer, serviceRemote.getMessage(parameter(request, "arg")))
  }

  // same as doGet
  override def doPost(request: HttpServletRequest, response: HttpServletResponse) = {
    doGet(request, response)
  }

  // an instance of TestService (generic method on Lookup)
  def service = lookup.local(serviceName, classOf[TestService])

  // a remote instance of TestService
  def serviceRemote = lookup.remote(serviceName, classOf[TestService])

  // a lookup utility, this is a servlet, so get a new instance each time this is called
  def lookup = new Lookup();

  // gets the lookup name for the service from the servlet configuration
  def serviceName = getServletConfig.getInitParameter(serviceNameParam)

  // accepts a PrintWriter, and prints the value argument to it
  def print(out: PrintWriter, value: String) = out.println(value)

  // fetches a request parameter by name
  def parameter(request: HttpServletRequest, name: String) = request.getParameter(name)

  val serviceNameParam = "test-service-name";
}