#! /bin/sh

echo
echo UnRarX build script version 1.0
echo By Daniel Aarno
echo
echo This script tries to build the UnRarX application from sources. Make sure you have Apples developer tools installed.
echo

#################################################

echo Building unrar
cd apps/unrar
rm unrar
make clean
make
echo Building unrar - done!

echo Building par2
cd ../par2cmdline-0.3a
make clean
sh configure
make
echo Building par2 - done!

echo Building UnRarX
cd ../..
pbxbuild
echo Building UnRarX - done!

##################################################

echo Build is completed. Check the build directory for you UnRarX application.