#ifndef _RAR_OS_
#define _RAR_OS_

#define FALSE 0
#define TRUE  1

#ifdef __EMX__
  #define INCL_BASE
#endif

#if defined(_WIN_32) || defined(_EMX)
#define ENABLE_BAD_ALLOC
#endif


#if defined(_WIN_32) || defined(_EMX)

#define LITTLE_ENDIAN
#define NM  1024

#ifdef _WIN_32

#define STRICT
#define WINVER 0x0400
#define _WIN32_WINNT 0x0300
//#define _WIN32_IE 0x0300
#define WIN32_LEAN_AND_MEAN

#include <windows.h>
#include <prsht.h>

#endif

#include <sys/types.h>
#include <sys/stat.h>
#include <dos.h>

#if !defined(_EMX) && !defined(_MSC_VER)
  #define ENABLE_MKTEMP
  #include <dir.h>
#endif
#ifdef _MSC_VER
  #define for if (0) ; else for
  #include <direct.h>
#else
  #include <dirent.h>
#endif

#include <share.h>

#ifdef ENABLE_BAD_ALLOC
  #include <new.h>
#endif

#ifdef _EMX
  #include <unistd.h>
  #include <pwd.h>
  #include <grp.h>
  #include <errno.h>
  #ifdef _DJGPP
    #include <utime.h>
  #else
    #include <os2.h>
    #include <sys/utime.h>
    #include <emx/syscalls.h>
  #endif
#else
  #ifdef _MSC_VER
    #include <exception>
  #else
    #include <except.h>
  #endif
#endif

#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <ctype.h>
#include <fcntl.h>
#include <dos.h>
#include <io.h>
#include <time.h>
#include <signal.h>

/*
#ifdef _WIN_32
#pragma hdrstop
#endif
*/

#define ENABLE_ACCESS

#define DefConfigName  "rar.ini"
#define DefLogName     "rar.log"

#ifdef _EMX
  #define HOST_OS     MS_DOS
#else
  #define HOST_OS     WIN_32
  #define ENABLE_CHANGE_PRIORITY
#endif

#define PATHDIVIDER  "\\"
#define PATHDIVIDERW L"\\"
#define CPATHDIVIDER '\\'
#define MASKALL      "*"
#define MASKALLW     L"*"

#define READBINARY   "rb"
#define READTEXT     "rt"
#define UPDATEBINARY "r+b"
#define CREATEBINARY "w+b"
#define APPENDTEXT   "at"

#if defined(_WIN_32)
  #ifdef _MSC_VER
    #define _stdfunction __cdecl
  #else
    #define _stdfunction _USERENTRY
  #endif
#else
  #define _stdfunction
#endif

#endif

#ifdef _UNIX

#define  NM  1024

#ifdef _BEOS
#include <be/kernel/fs_info.h>
#include <be/kernel/fs_attr.h>
#endif

#include <unistd.h>
#include <sys/types.h>
#include <sys/stat.h>
#include <sys/file.h>
#if defined(__FreeBSD__) || defined (__NetBSD__) || defined (__OpenBSD__) || defined(__APPLE__)
  #include <sys/param.h>
  #include <sys/mount.h>
#else
#endif
#include <pwd.h>
#include <grp.h>
#include <stdio.h>
#include <stdlib.h>
#include <stdarg.h>
#include <string.h>
#include <ctype.h>
#include <fcntl.h>
#include <errno.h>
#include <dirent.h>
#include <time.h>
#include <signal.h>
#include <utime.h>
#include <locale.h>

#ifdef  S_IFLNK
#define SAVE_LINKS
#endif

#define ENABLE_ACCESS

#define DefConfigName  ".rarrc"
#define DefLogName     ".rarlog"

#ifdef _BEOS
#define HOST_OS     BEOS
#else
#define HOST_OS     UNIX
#endif

#define PATHDIVIDER  "/"
#define PATHDIVIDERW L"/"
#define CPATHDIVIDER '/'
#define MASKALL      "*"
#define MASKALLW     L"*"

#define READBINARY   "r"
#define READTEXT     "r"
#define UPDATEBINARY "r+"
#define CREATEBINARY "w+"
#define APPENDTEXT   "a"

#define _stdfunction 

#ifdef _APPLE
	#ifndef BIG_ENDIAN
		#define BIG_ENDIAN
	#endif
	#ifdef LITTLE_ENDIAN
		#undef LITTLE_ENDIAN
	#endif
#endif

#if defined(__sparc) || defined(sparc)
  #ifndef BIG_ENDIAN
     #define BIG_ENDIAN
  #endif
#endif

#endif

typedef const char* MSGID;

#define safebuf static

#if defined(LITTLE_ENDIAN) && defined(BIG_ENDIAN)
  #if defined(BYTE_ORDER) && BYTE_ORDER == BIG_ENDIAN
    #undef LITTLE_ENDIAN
  #elif defined(BYTE_ORDER) && BYTE_ORDER == LITTLE_ENDIAN
    #undef BIG_ENDIAN
  #else
    #error "Both LITTLE_ENDIAN and BIG_ENDIAN are defined. Undef something one"
  #endif
#endif


#endif // _RAR_OS_
