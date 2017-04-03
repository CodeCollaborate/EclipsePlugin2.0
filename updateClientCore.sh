#!/bin/bash

pushd org.code.toboggan.clientcore/
gradle clean build unpackSources --refresh-dependencies
