package com.afei.akkaangular

import akka.actor.ActorSystem
import com.afei.akkaangular.config.{ ServerConfig, CoreConfig }
import com.afei.akkaangular.email.{ EmailTemplatingEngine, EmailConfig, SmtpEmailService, DummyEmailService }
import com.afei.akkaangular.passwordreset.{ PasswordResetCodeDao, PasswordResetService }
import com.afei.akkaangular.sql.{ DatabaseConfig, SqlDatabase }
import com.afei.akkaangular.user.rememberme.{ RememberMeTokenDao, RefreshTokenStorageImpl }
import com.afei.akkaangular.user.{ UserDao, UserService }
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import scala.concurrent.ExecutionContext

trait Beans extends StrictLogging {
  def system: ActorSystem
  implicit def ec: ExecutionContext

  lazy val config = new CoreConfig with EmailConfig with DatabaseConfig with ServerConfig {
    override def rootConfig = ConfigFactory.load()
  }

  lazy val userDao = new UserDao(sqlDatabase)

  lazy val codeDao = new PasswordResetCodeDao(sqlDatabase)

  lazy val rememberMeTokenDao = new RememberMeTokenDao(sqlDatabase)

  lazy val sqlDatabase = SqlDatabase.create(config)

  lazy val emailService = if (config.emailEnabled) {
    new SmtpEmailService(config)
  } else {
    logger.info("Starting with fake email sending service. No emails will be sent.")
    new DummyEmailService
  }

  lazy val emailTemplatingEngine = new EmailTemplatingEngine

  lazy val userService = new UserService(
    userDao,
    emailService,
    emailTemplatingEngine
  )

  lazy val passwordResetService = new PasswordResetService(
    userDao,
    codeDao,
    emailService,
    emailTemplatingEngine,
    config
  )

  lazy val refreshTokenStorage = new RefreshTokenStorageImpl(rememberMeTokenDao, system)
}
