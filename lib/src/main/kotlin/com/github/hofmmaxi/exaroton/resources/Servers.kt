package com.github.hofmmaxi.exaroton.resources

import io.ktor.resources.*

@Resource("/servers")
internal class Servers {
    @Resource("{id}")
    class Id(val parent: Servers = Servers(), val id: String) {
        @Resource("logs")
        class Logs(val parent: Id) {
            @Resource("share")
            class Share(val parent: Logs)
        }

        @Resource("options")
        class Options(val parent: Id) {
            @Resource("motd")
            class Motd(val parent: Options)

            @Resource("ram")
            class Ram(val parent: Options)
        }

        @Resource("start")
        class Start(val parent: Id)

        @Resource("stop")
        class Stop(val parent: Id)

        @Resource("restart")
        class Restart(val parent: Id)

        @Resource("command")
        class Command(val parent: Id)

        @Resource("playerlists")
        class PlayerLists(val parent: Id) {
            @Resource("{list}")
            class List(val parent: PlayerLists, val id: String)
        }

        @Resource("files")
        class Files(val parent: Id) {
            @Resource("info")
            class Info(val parent: Files) {
                @Resource("{path}")
                class Path(val parent: Info, val path: String)
            }

            @Resource("data")
            class Data(val parent: Files) {
                @Resource("{path}")
                class Path(val parent: Data, val path: String)
            }
        }

        @Resource("websocket")
        class WebSocket(val parent: Id)
    }
}