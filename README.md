# M3

M3 Project is the distribute storage module of DATA Project.

Here is the introduction of the directory structure.

## Poject Structure

### routing

Routing submodule is the base module of the DHT network.

It currently mainly use the Kademlia network. 

The code of Kademlia are derived from the 
[Joshua Kisson's Kademlia Project](https://github.com/JoshuaKissoon/Kademlia).

In order used in mobile network, data structures and low level communication protocol 
will be modified.

Also some bugs in the origin code has been fixed.

### routing-api

This is the main API to access the distributed network.

### EC

The Erasure Encoding library for the routing.

### web

The web application for hosting a M3 node in a J2EE Container.

### cli

A command line toolset to maintain the web app.

### android

An Android client example to use M3 client on the Android Devices.

### ios

An iOS client example to use M3 client on the iOS devices.      

## Compile

The project uses the gradle build tool to maintain codes.

We recommend you to use the IntelliJ IDEA the use the project.
