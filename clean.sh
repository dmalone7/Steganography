#!/bin/bash

echo "Removing .class files..."
rm $(find -type f -name "*.class")

echo "Removing \"-steg\" images..."
rm $(find -type f -name '*-steg*')

echo "Removing \"-out\" files..."
rm $(find -type f -name '*-out*')