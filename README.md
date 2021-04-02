# Pitch-Black-TeamProject
Race though a dark dungeon as an explorer. Dubbed as the "Pitch Black" by archaic explorers of the past, escape the non-stop approach of poisonous fog among other explorers in a bid for freedom! After fighting you way though the dark depths, you and your team of explorers finally eye-out treasures beyond your wildest dreams! Basking in your newfound riches, you unknowingly trigger a curse. One which has you fighting against your spelunkers in fear of your lives!

This repo was made by Team 8 for the module 'CS2020 Team Project'. Find more details, as well as the members involved, and our blog, on our website at http://www.pitchblack.getenjoyment.net

# Game Controls:
A: Move Left

D: Move right

Space: Jump

# Frameworks
Our project was coded in java, whilst the server was made in javascript. The front-end and back-end logic was made using the libGDX framework.
Our network layer was achieved using node.js and socket.io, as well as the the socket.io client java implementation. Gradle was our choice for software development involving dependency management and deployment.

# Requirements
Java 11+ JRE (11 recommended)

A substantial internet connection (>3mbps)

# Backend
  NB: Want to run the game locally? Well you can! (albeit quite fiddily, as the backend is not compiled)
  1. Ensure XAMPP, Node.js and Java (11+) is installed as well as the source code (this is the back-end, in master branch or zipped folder under releases v1.0)
  2. Run XAMPP for Apache and SQL.
  3. Go to localhost in browser, open phpmyadmin, and run the sql commands as seen in the file 'SQLtoSetupDatabase.sql'. A 'gamedatabase' database should be created with a 'users ' table.
  4. Run npm install though the terminal inside the server folder
  5. Run the file 'runBackend.bat' in the root folder
  6. Run the client as normal!
