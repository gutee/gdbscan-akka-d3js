package com.afei.akkaangular.config

import com.afei.akkaangular.common.ConfigWithDefault
import com.typesafe.config.Config

trait CoreConfig extends ConfigWithDefault {
  def rootConfig: Config

  lazy val resetLinkPattern = getString("akkaangular.reset-link-pattern", "http://localhost:8080/#/password-reset?code=%s")
}
