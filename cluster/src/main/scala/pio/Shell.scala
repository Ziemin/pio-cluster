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

import akka.actor.ActorRef
import akka.actor.ActorSystem
import akka.cluster.Cluster
import akka.actor.RootActorPath
import akka.util.Timeout
import scala.concurrent.duration._
import scala.concurrent.Await
import akka.pattern.Patterns


object Shell {

  implicit val timeout = Timeout(5.seconds)

  val system = ActorSystem("pio-cluster")

  def deploy(admin: ActorRef, engine: String): Unit = {
    Patterns.ask(admin, Admin.DeployEngine(engine), timeout)
  }

  def undeploy(admin: ActorRef, engine: String): Unit = {
    Patterns.ask(admin, Admin.UndeployEngine(engine), timeout)
  }

  def main(args: Array[String]): Unit = {

    val address = Cluster(system).state.members
      .find(m => m.hasRole(Admin.role)).get.address
    val admin = Await.result(
      system.actorSelection(RootActorPath(address) / "user" / "admin").resolveOne,
      timeout.duration)
      .asInstanceOf[ActorRef]

    while(true) {
      val commands = scala.io.StdIn.readLine.split(" ")
      commands(0) match {
        case "deploy" => deploy(admin, commands(1))
        case "undeploy" => undeploy(admin, commands(1))
      }
    }
  }
}
