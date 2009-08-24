package net.scalaejb.util

import java.lang.{Class, String}
import javax.naming.{InitialContext, Context}

/**
 * @author justin
 */
class Lookup {

  // returns a local instance (generic)
  def local[T](mappedName: String, expected: Class[T]) = lookup(nameLocal(mappedName), expected)

  // returns a local instance
  def local(mappedName: String): AnyRef = lookup(nameLocal(mappedName))

  // return a remote instance (generic)
  def remote[T](name: String, expected: Class[T]) = lookup(nameRemote(name), expected)

  // return a remote instance
  def remote(mappedName: String): AnyRef = lookup(nameRemote(mappedName))

  // lookup an object from jndi (generic)
  def lookup[T](name: String, expected: Class[T]): T = lookup(name).asInstanceOf[T]

  // look up an object from jndi
  def lookup(name: String): AnyRef = context.lookup(name)

  // a jndi context
  val context = new InitialContext();

  // builds a local jndi path
  def nameLocal(mappedName: String) = mappedName + "/" + suffixLocal

  // builds a remote jndi path
  def nameRemote(mappedName: String) = mappedName + "/" + suffixRemote

  val suffixLocal = "local"
  val suffixRemote = "remote"
}