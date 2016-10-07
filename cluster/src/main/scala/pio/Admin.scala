/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package pio

import akka.actor.Actor
import akka.actor.ActorRef
import akka.actor.ActorLogging
import akka.cluster.Cluster
import akka.cluster.Member
import akka.cluster.ClusterEvent._
import akka.actor.RootActorPath
import akka.util.Timeout
import scala.concurrent.duration._


object Admin {

  sealed abstract class AdminCommand
  case class DeployEngine(name: String) extends AdminCommand
  case class UndeployEngine(name: String) extends AdminCommand

  val role: String = "admin"

  def main(args: Array[String]): Unit = {

  }
}

class Admin extends Actor with ActorLogging {
  import Admin._
  import context.dispatcher

  val cluster = Cluster(context.system)

  private var registryService: Option[ActorRef] = None

  override def preStart(): Unit = cluster.subscribe(self, classOf[MemberUp])
  override def postStop(): Unit = cluster.unsubscribe(self)

  def receive = {
    case DeployEngine(name) =>
      log.info("Deploy engine command: {}", name)
    case UndeployEngine(name) =>
      log.info("Undeploy engine command {}", name)

    case MemberUp(m) => register(m)
  }

  def register(member: Member): Unit = {
    if (member.hasRole(Registry.role)) {
      context.actorSelection(RootActorPath(member.address) / "user" / "registry")
        .resolveOne(Timeout(5.seconds).duration)
        .map { actorRef =>
          log.info("Obtained registry service at {}", member.address)
          registryService = Some(actorRef)
        }
    }
  }
}
