#!/bin/bash

echo "Setting CLASSPATH..."

export CLASSPATH=$CLAASPATH:"./src/"

echo "Compiling all .java files..."
javac $(find . -name "*.java")

echo "Running Encrypt..."
java Steganography -E pupper.jpg my-message

echo "Running Decrypt..."
java Steganography -D pupper-steg.png my-message-out
