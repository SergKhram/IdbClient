# IdbClient
It is the way to get access to the idb programmatically, using kotlin. You can control Apple devices and simulators from 
your code with this client, which creates a connection to them by grpc. All you need to set up is to install the idb companion
(https://fbidb.io/docs/installation) on your Mac and run it. You can use idb client connection from any architecture.

## Functionality
We provide commands' wrappers to functionality of idb companion(you can check the full list here https://fbidb.io/docs/commands)

## How to
1. Create idbClient: 
```
val idb = IOSDebugBridgeClient()
```
2. Connect companion:
```
val udid = idb.connectToCompanion(TcpAddress("127.0.0.1", 10882))
```
3. Execute request:
```
val result = idb.execute(
    LogRequest(
        predicate = {false}, 
        timeout = Duration.ofSeconds(10)
    ),
    udid
)
result.forEach(::println)
```

## LICENSE
```
Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```