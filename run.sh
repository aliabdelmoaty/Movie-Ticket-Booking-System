#!/bin/bash
# Script to run Movie Ticket Booking System

echo "🎬 Starting Movie Ticket Booking System..."
echo ""

# Check if compiled
if [ ! -d "bin" ] || [ ! -f "bin/Main.class" ]; then
    echo "⚙️ Compiling project..."
    javac -cp ".:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" -d bin -sourcepath src src/database/*.java src/model/*.java src/core/*.java src/gui/*.java src/Main.java
    echo "✅ Compilation complete!"
    echo ""
fi

# Run the application
echo "🚀 Launching application..."
java -cp "bin:sqlite-jdbc.jar:slf4j-api.jar:slf4j-simple.jar" Main

echo ""
echo "👋 Application closed."

