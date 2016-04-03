package com.afei.akkaangular.test

import com.afei.akkaangular.config.CoreConfig
import com.afei.akkaangular.email.{DummyEmailService, EmailTemplatingEngine}
import com.afei.akkaangular.sql.SqlDatabase
import com.afei.akkaangular.user.{User, UserDao, UserService}
import com.typesafe.config.ConfigFactory
import org.scalatest.concurrent.ScalaFutures

import scala.concurrent.ExecutionContext

trait TestHelpersWithDb extends TestHelpers with ScalaFutures {

  lazy val emailService = new DummyEmailService()
  lazy val emailTemplatingEngine = new EmailTemplatingEngine
  lazy val userDao = new UserDao(sqlDatabase)
  lazy val userService = new UserService(userDao, emailService, emailTemplatingEngine)
  lazy val config = new CoreConfig {
    override def rootConfig = ConfigFactory.load()
  }

  def sqlDatabase: SqlDatabase
  implicit lazy val ec: ExecutionContext = scala.concurrent.ExecutionContext.Implicits.global

  def newRandomStoredUser(password: Option[String] = None): User = {
    val u = newRandomUser(password)
    userDao.add(u).futureValue
    u
  }
}
