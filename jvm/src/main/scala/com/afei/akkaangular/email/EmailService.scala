package com.afei.akkaangular.email

import scala.concurrent.Future

trait EmailService {

  def scheduleEmail(address: String, emailData: EmailContentWithSubject): Future[Unit]

}
