par2 build notes


Building

% ./configure
% make
% strip par2


Patches

For UnRarX, par2 currently produces too much progress feedback (0.1% increments), and does so that each progress step appears on a new line.  In a terminal, it returns and overwrites the previous step, but the command shell that UnRarX uses doesn't understand that.  Until the author adds a quiet[er] option (in the works I'm told), the following files have had the progress code changed to 10% steps, on a single line.  Some I had to move to the end of loops so that it would show 100%.

par1repairer.cpp
par2creator.cpp
par2creatorsourcefile.cpp
par2repairer.cpp
reedsolomon.h


No details at this time.


- William Kyngesburye
  kyngchaos@charter.net
