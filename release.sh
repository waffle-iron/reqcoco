#!/bin/bash

export GPG_TTY=$(tty)
mvn clean release:clean release:prepare -P release release:perform
