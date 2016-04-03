package com.afei.akkaangular.sql

import com.afei.akkaangular.common.ConfigWithDefault
import com.afei.akkaangular.sql.DatabaseConfig._
import com.typesafe.config.Config

trait DatabaseConfig extends ConfigWithDefault {
  def rootConfig: Config

  // format: OFF
  lazy val dbH2Url              = getString(s"akkaangular.db.h2.properties.url", "jdbc:h2:file:./data/akkaangular")
  lazy val dbPostgresServerName = getString(PostgresServerNameKey, "")
  lazy val dbPostgresPort       = getString(PostgresPortKey, "5432")
  lazy val dbPostgresDbName     = getString(PostgresDbNameKey, "")
  lazy val dbPostgresUsername   = getString(PostgresUsernameKey, "")
  lazy val dbPostgresPassword   = getString(PostgresPasswordKey, "")
}

object DatabaseConfig {
  val PostgresDSClass       = "akkaangular.db.postgres.dataSourceClass"
  val PostgresServerNameKey = "akkaangular.db.postgres.properties.serverName"
  val PostgresPortKey       = "akkaangular.db.postgres.properties.portNumber"
  val PostgresDbNameKey     = "akkaangular.db.postgres.properties.databaseName"
  val PostgresUsernameKey   = "akkaangular.db.postgres.properties.user"
  val PostgresPasswordKey   = "akkaangular.db.postgres.properties.password"
  // format: ON
}