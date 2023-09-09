<h1 style="text-align: center">
 Exaroton API Client 
</h1>


---

## About

This is an unofficial library for the [exaroton API](https://developers.exaroton.com/), which you can use to manage your
servers.
Since the official library doesn't deliver good type safety, this library was written entirely in Kotlin using Ktor and
took some inspiration from the [kord library](https://github.com/kordlib/kord), but has the same Classes and methods
like the official library.

It's currently still WIP, therefore some help would be really appreciated. ^^

## Features

- [x] Requesting Account Data
- [x] Executing Server Commands
- [x] Create, retrieve and update server files
- [x] Retrieve server logs
- [x] Retrieve server infos like software, MOTD, address, etc.
- [x] Get info about the Players
- [x] Get and set the RAM for the server
- [x] Start, stop and restart the server
- [x] View the servers status

## Still missing

- [ ] Get and update config options
- [ ] WebSocket endpoints
- [ ] Test for all methods + mocking client

## Development

You are very welcome to contribute to this project.
Visit [this page](https://developers.exaroton.com) to read the API documentation for exaroton.

Also, feel free to contact me on [Discord](https://discord.com/users/463044315007156224) (@maxii)
or [E-mail](mailto:mh@wildemail.de)!