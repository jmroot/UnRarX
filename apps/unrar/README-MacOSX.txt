Mac OS X notes for UnRarX


Building unrar

rename makefile.unix to makefile, then make

don't use rarlab's Mac OS X unrar binary, because of the next note.


Changes to unrar

In it's current state, on Mac OS X, if unrar encounters a rar archive created with the old MacRar 2.5, and that archive contains files with accented characters in their names, the file system will generate a "Could not create file" error and unrar will exit without extracting anything.  I think this also applies to the rar v2.5 format in general, for say Windows ASCII.

The problem is that the Mac ASCII encoding is not being converted to UTF8.

My quick fix patches Archive::ConvertUnknownHeader() in arcread.cpp so those character are changed to an underscore.  It really needs an encoding conversion, preferrably simple and without using external library so the author might officially accept it, but this is beyond my C skills.

Details of patch (sorry, I haven't figured out the diff thing yet):

replace the whole #if block in Archive::ConvertUnknownHeader() with:

#if defined(_APPLE)
// && !defined(UNICODE_SUPPORTED)
    if (NewLhd.HostOS != UNIX && NewLhd.HostOS != BEOS)
    {
      if ((byte)*s<32 || (byte)*s>127)
        *s='_';
    }
#endif


- William Kyngesburye
  kyngchaos@charter.net
  