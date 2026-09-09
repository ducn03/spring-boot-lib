---
name: springboot-lib-realtime-state
description: 'Work on WebSocket sessions and rooms, socket message routing, EventBus events, or the reusable manual state machine in this Spring Boot project.'
---

# Realtime and State Workflow

## WebSocket Flow

The endpoint is `/webSocket`, configured by `WebsocketConfig`. Follow the actual pipeline `MessageDecoder -> SocketConnector -> SocketServer -> room manager`. `SocketConnector` manually parses JSON into `Message` subclasses; `SocketServer` routes `CartMessage`, `ChatMessage`, and `NotifyMessage`.

When changing this path:

1. Preserve the required non-empty query-string signature unless authentication is intentionally redesigned.
2. Keep local lifecycle in `SessionManager` and Redis-backed membership in the relevant `RoomManager`.
3. Check synchronized access, disconnect cleanup, and whether a method returns a mutable internal set.
4. Test malformed JSON, unknown message types, missing signatures, closed sessions, and broadcast behavior.

`CartRoomManager` and `NotifyRoomManager` are implemented with Redis hashes plus local sessions. `ChatRoomManager` is currently unimplemented; a declared message type does not imply a working chat room.

## EventBus

The working event path is a synchronous in-process consumer map in `EventBus`; `EventFactory` registers the `TEST` handler. `KafkaProducer` is empty, so do not describe this as durable/asynchronous Kafka processing.

## State Machine

The reusable API is in `com.springboot.lib.sm`: `Template`, `State`, `Action`, `StateMachine`, `ManualStateMachine`, `SMInput`, `SMData`, and `ActionCallback`. `ManualStateMachine.handle` loads persisted data, validates state/action, sets the next status, runs the callback, and saves in `finally`.

Preserve dedicated error codes for missing input/state/action. Test successful transitions, invalid transitions, callback exceptions, and the persistence guarantee from `finally`.
