package controllers

import javax.inject.Inject
import play.api.http.FileMimeTypes
import play.api.i18n.{Langs, MessagesApi}
import play.api.mvc.{ControllerComponents, DefaultActionBuilder, PlayBodyParsers}

case class ClusterControllerComponents @Inject()(service: ClusterService,
                                                 actionBuilder: DefaultActionBuilder,
                                                 parsers: PlayBodyParsers,
                                                 messagesApi: MessagesApi,
                                                 langs: Langs,
                                                 fileMimeTypes: FileMimeTypes,
                                                 executionContext: scala.concurrent.ExecutionContext)
  extends ControllerComponents
